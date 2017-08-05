/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.core.network.mtp.actors;

import com.google.j2objc.annotations.AutoreleasePool;

import java.io.IOException;

import im.actor.core.network.ActorApi;
import im.actor.core.network.Endpoints;
import im.actor.core.network.NetworkState;
import im.actor.core.network.mtp.MTProto;
import im.actor.core.network.mtp.entity.EncryptedCBCPackage;
import im.actor.core.network.mtp.entity.EncryptedPackage;
import im.actor.core.network.mtp.entity.ProtoMessage;
import im.actor.runtime.Crypto;
import im.actor.runtime.Log;
import im.actor.runtime.Network;
import im.actor.runtime.actors.Actor;
import im.actor.runtime.actors.ActorCancellable;
import im.actor.runtime.actors.ActorRef;
import im.actor.runtime.actors.ActorSelection;
import im.actor.runtime.actors.ActorSystem;
import im.actor.runtime.actors.Props;
import im.actor.runtime.bser.DataInput;
import im.actor.runtime.bser.DataOutput;
import im.actor.runtime.crypto.ActorProtoKey;
import im.actor.runtime.crypto.box.CBCHmacBox;
import im.actor.runtime.crypto.primitives.kuznechik.KuznechikFastEngine;
import im.actor.runtime.crypto.primitives.streebog.Streebog256;
import im.actor.runtime.crypto.primitives.util.ByteStrings;
import im.actor.runtime.mtproto.Connection;
import im.actor.runtime.mtproto.ConnectionCallback;
import im.actor.runtime.mtproto.CreateConnectionCallback;
import im.actor.runtime.threading.AtomicIntegerCompat;
import im.actor.runtime.util.ExponentialBackoff;

/**
 * Possible problems
 * * Creating connections after actor kill
 */
public class ManagerActor extends Actor {

    private static final String TAG = "Manager";

    public static ActorRef manager(final MTProto mtProto) {
        return ActorSystem.system().actorOf(
                new ActorSelection(Props.create(() -> new ManagerActor(mtProto)).changeDispatcher("network_manager"), mtProto.getActorPath() + "/manager"));
    }

    private static final AtomicIntegerCompat NEXT_CONNECTION = im.actor.runtime.Runtime.createAtomicInt(1);

    private final MTProto mtProto;
    private final Endpoints endpoints;
    private final long authId;
    private final byte[] authKey;
    private final ActorProtoKey authProtoKey;
    private final CBCHmacBox serverUSDecryptor;
    private final CBCHmacBox serverRUDecryptor;
    private final CBCHmacBox clientUSEncryptor;
    private final CBCHmacBox clientRUEncryptor;
    private final long sessionId;
    private final boolean isEnableLog;

    // Connection
    private int currentConnectionId;
    private Connection currentConnection;
    private int networkState = NetworkState.UNKNOWN;
    private int outSeq = 0;
    private int inSeq = 0;

    // Creating
    private boolean isCheckingConnections = false;
    private final ExponentialBackoff backoff;
    private ActorCancellable checkCancellable;

    private ActorRef receiver;
    private ActorRef sender;

    public ManagerActor(MTProto mtProto) {
        this.mtProto = mtProto;
        this.endpoints = mtProto.getEndpoints();
        this.authId = mtProto.getAuthId();
        this.authKey = mtProto.getAuthKey();
        if (this.authKey != null) {
            this.authProtoKey = new ActorProtoKey(this.authKey);
            this.serverUSDecryptor = new CBCHmacBox(
                    Crypto.createAES128(this.authProtoKey.getServerKey()),
                    Crypto.createSHA256(),
                    this.authProtoKey.getServerMacKey());
            this.serverRUDecryptor = new CBCHmacBox(
                    new KuznechikFastEngine(this.authProtoKey.getServerRussianKey()),
                    new Streebog256(),
                    this.authProtoKey.getServerMacRussianKey());
            this.clientUSEncryptor = new CBCHmacBox(
                    Crypto.createAES128(this.authProtoKey.getClientKey()),
                    Crypto.createSHA256(),
                    this.authProtoKey.getClientMacKey());
            this.clientRUEncryptor = new CBCHmacBox(
                    new KuznechikFastEngine(this.authProtoKey.getClientRussianKey()),
                    new Streebog256(),
                    this.authProtoKey.getClientMacRussianKey());
        } else {
            this.authProtoKey = null;
            this.serverUSDecryptor = null;
            this.serverRUDecryptor = null;
            this.clientUSEncryptor = null;
            this.clientRUEncryptor = null;
        }
        this.sessionId = mtProto.getSessionId();
        this.isEnableLog = mtProto.isEnableLog();
        backoff = new ExponentialBackoff(mtProto.getMinDelay(), mtProto.getMaxDelay(), mtProto.getMaxFailureCount());
    }

    @Override
    public void preStart() {
        receiver = ReceiverActor.receiver(mtProto);
        sender = PusherActor.senderActor(mtProto);
        connectionStateChanged();
        checkConnection(false);
    }

    @Override
    public void postStop() {
        this.receiver = null;
        this.sender = null;
        currentConnectionId = -1;
        if (currentConnection != null) {
            currentConnection.close();
            currentConnection = null;
        }
        connectionStateChanged();
    }

    @Override
    public void onReceive(Object message) {

        // Connections
        if (message instanceof ConnectionCreated) {
            ConnectionCreated c = (ConnectionCreated) message;
            onConnectionCreated(c.connectionId, c.connection);
        } else if (message instanceof ConnectionCreateFailure) {
            onConnectionCreateFailure();
        } else if (message instanceof ConnectionDie) {
            onConnectionDie(((ConnectionDie) message).connectionId);
        } else if (message instanceof PerformConnectionCheck) {
            PerformConnectionCheck connectionCheck = (PerformConnectionCheck) message;
            checkConnection(connectionCheck.ignoreNetworkState);
        } else if (message instanceof NetworkChanged) {
            onNetworkChanged(((NetworkChanged) message).state);
        } else if (message instanceof ForceNetworkCheck) {
            forceNetworkCheck();
        }
        // Messages
        else if (message instanceof OutMessage) {
            OutMessage m = (OutMessage) message;
            onOutMessage(m.message, m.offset, m.len);
        } else if (message instanceof InMessage) {
            InMessage m = (InMessage) message;
            onInMessage(m.data, m.offset, m.len);
        } else {
            super.onReceive(message);
        }
    }

    private void onConnectionCreated(int id, Connection connection) {

        isCheckingConnections = false;

        backoff.onSuccess();

        if (connection.isClosed()) {
            if (isEnableLog) {
                Log.w(TAG, "Unable to register connection #" + id + ": already closed");
            }
            return;
        }

        if (currentConnectionId == id) {
            if (isEnableLog) {
                Log.w(TAG, "Unable to register connection #" + id + ": already have connection");
            }
            return;
        }

        Log.d(TAG, "Connection #" + id + " created");

        if (currentConnection != null) {
            currentConnection.close();
            currentConnection = null;
            currentConnectionId = -1;
        }

        currentConnectionId = id;
        currentConnection = connection;
        outSeq = 0;
        inSeq = 0;

        connectionStateChanged();

        requestCheckConnection();
        sender.send(new PusherActor.ConnectionCreated());
    }

    private void onConnectionCreateFailure() {
        Log.w(TAG, "Connection create failure");
        isCheckingConnections = false;
        currentConnectionId = -1;
        currentConnection = null;
        backoff.onFailure();
        requestCheckConnection(backoff.exponentialWait());
    }

    private void onConnectionDie(int id) {
        Log.w(TAG, "Connection #" + id + " dies");
        if (currentConnectionId == id) {
            currentConnectionId = -1;
            currentConnection = null;
            outSeq = 0;
            inSeq = 0;
            isCheckingConnections = false;
            connectionStateChanged();
            requestCheckConnection();
        }
    }

    private void onNetworkChanged(int state) {
        Log.w(TAG, "Network configuration changed: " + NetworkState.getDesription(state));
        this.networkState = state;
        backoff.reset();
        checkConnection(false);
    }

    private void forceNetworkCheck() {
        if (currentConnection != null) {
            currentConnection.checkConnection();
        }else{
            self().send(new PerformConnectionCheck(true));
        }
    }

    private void requestCheckConnection() {
        requestCheckConnection(0);
    }

    private void requestCheckConnection(long wait) {
        Log.d(TAG, "isCheckingConnections: "+isCheckingConnections);

        if (!isCheckingConnections) {
            if (currentConnection == null) {
                if (wait == 0) {
                    Log.w(TAG, "Requesting connection creating");
                } else {
                    Log.w(TAG, "Requesting connection creating in " + wait + " ms");
                }
            }
            if (checkCancellable != null) {
                checkCancellable.cancel();
                checkCancellable = null;
            }
            checkCancellable = schedule(new PerformConnectionCheck(), wait);
        }
    }

    private void checkConnection(boolean forceCheck) {
        Log.d(TAG, "isCheckingConnections: "+isCheckingConnections);

        if (isCheckingConnections && !forceCheck) {
            return;
        }

        Log.d(TAG, "Checking Connection");

        if (currentConnection == null) {
            Log.d(TAG, "currentConnection is null");
            Log.d(TAG, "Connection networkState: "+NetworkState.getDesription(networkState));

            if ((networkState == NetworkState.NO_CONNECTION) && !forceCheck) {
                Log.d(TAG, "Not trying to create connection: Not network available");
                return;
            }

            Log.d(TAG, "Trying to create connection...");
            isCheckingConnections = true;

            final int id = NEXT_CONNECTION.getAndIncrement();

            Network.createConnection(id, ActorApi.MTPROTO_VERSION,
                    ActorApi.API_MAJOR_VERSION,
                    ActorApi.API_MINOR_VERSION,
                    endpoints.fetchEndpoint(authKey == null), new ConnectionCallback() {

                        @Override
                        public void onConnectionRedirect(String host, int port, int timeout) {
                            Log.d(TAG, "Connection redirect");
                            self().send(new ConnectionDie(id));
                        }

                        @Override
                        public void onMessage(byte[] data, int offset, int len) {
                            self().send(new InMessage(data, offset, len));
                        }

                        @Override
                        public void onConnectionDie() {
                            Log.d(TAG, "Connection die");
                            self().send(new ConnectionDie(id));
                        }
                    }, new CreateConnectionCallback() {
                        @Override
                        public void onConnectionCreated(Connection connection) {
                            self().send(new ConnectionCreated(id, connection));
                        }

                        @Override
                        public void onConnectionCreateError() {
                            self().send(new ConnectionCreateFailure());
                        }
                    });
        }
    }

    private void connectionStateChanged() {
        mtProto.getCallback().onConnectionsCountChanged(currentConnection != null ? 1 : 0);
    }

    @AutoreleasePool
    private void onInMessage(byte[] data, int offset, int len) {
        DataInput bis = new DataInput(data, offset, len);
        try {
            long authId = bis.readLong();
            long sessionId = bis.readLong();

            if (authId != this.authId || sessionId != this.sessionId) {
                throw new IOException("Incorrect header");
            }

            if (authKey != null) {
                EncryptedPackage encryptedPackage = new EncryptedPackage(bis);
                int seq = (int) encryptedPackage.getSeqNumber();
                if (seq != inSeq) {
                    throw new IOException("Expected " + inSeq + ", got: " + seq);
                }
                inSeq++;
                // long start = Runtime.getActorTime();
                EncryptedCBCPackage usEncryptedPackage = new EncryptedCBCPackage(new DataInput(encryptedPackage.getEncryptedPackage()));
                byte[] ruPackage = serverUSDecryptor.decryptPackage(ByteStrings.longToBytes(seq), usEncryptedPackage.getIv(), usEncryptedPackage.getEncryptedContent());
                EncryptedCBCPackage ruEncryptedPackage = new EncryptedCBCPackage(new DataInput(ruPackage));
                byte[] plainText = serverRUDecryptor.decryptPackage(ByteStrings.longToBytes(seq), ruEncryptedPackage.getIv(), ruEncryptedPackage.getEncryptedContent());

                // Log.d(TAG, "Package decrypted in " + (Runtime.getActorTime() - start) + " ms, size: " + len);

                DataInput ptInput = new DataInput(plainText);
                long messageId = ptInput.readLong();
                byte[] ptPayload = ptInput.readProtoBytes();
                receiver.send(new ProtoMessage(messageId, ptPayload));
            } else {
                long messageId = bis.readLong();
                byte[] payload = bis.readProtoBytes();
                receiver.send(new ProtoMessage(messageId, payload));
            }
        } catch (IOException e) {
            Log.w(TAG, "Closing connection: incorrect package");
            Log.e(TAG, e);

            if (currentConnection != null) {
                try {
                    currentConnection.close();
                } catch (Exception e2) {
                    Log.e(TAG, e2);
                }
                currentConnection = null;
                currentConnectionId = 0;
                outSeq = 0;
                inSeq = 0;
            }
            checkConnection(false);
        }
    }

    private void onOutMessage(byte[] data, int offset, int len) {

        if (currentConnection != null && currentConnection.isClosed()) {
            currentConnection = null;
            Log.d(TAG, "Cleaning bad connection #" + currentConnectionId);
            currentConnectionId = -1;
            outSeq = 0;
            inSeq = 0;
            checkConnection(false);
        }

        try {
            if (currentConnection != null) {
                if (authKey != null) {
                    int seq = outSeq++;
                    // long start = Runtime.getActorTime();
                    byte[] ruIv = new byte[16];
                    Crypto.nextBytes(ruIv);
                    byte[] usIv = new byte[16];
                    Crypto.nextBytes(usIv);

                    byte[] ruCipherText = clientRUEncryptor.encryptPackage(ByteStrings.longToBytes(seq), ruIv, ByteStrings.substring(data, offset, len));
                    byte[] ruPackage = new EncryptedCBCPackage(ruIv, ruCipherText).toByteArray();
                    byte[] usCipherText = clientUSEncryptor.encryptPackage(ByteStrings.longToBytes(seq), usIv, ruPackage);
                    byte[] usPackage = new EncryptedCBCPackage(usIv, usCipherText).toByteArray();

                    EncryptedPackage encryptedPackage = new EncryptedPackage(seq, usPackage);
                    byte[] cipherData = encryptedPackage.toByteArray();
                    DataOutput bos = new DataOutput();
                    bos.writeLong(authId);
                    bos.writeLong(sessionId);
                    bos.writeBytes(cipherData, 0, cipherData.length);
                    byte[] pkg = bos.toByteArray();
                    currentConnection.post(pkg, 0, pkg.length);
                } else {
                    DataOutput bos = new DataOutput();
                    bos.writeLong(authId);
                    bos.writeLong(sessionId);
                    bos.writeBytes(data, offset, len);
                    byte[] pkg = bos.toByteArray();
                    currentConnection.post(pkg, 0, pkg.length);
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "Closing connection: exception during push");
            Log.e(TAG, e);

            if (currentConnection != null) {
                try {
                    currentConnection.close();
                } catch (Exception e2) {
                    Log.e(TAG, e2);
                }
                currentConnection = null;
                currentConnectionId = 0;
                outSeq = 0;
                inSeq = 0;
            }
            checkConnection(false);
        }
    }

    public static class OutMessage {
        private byte[] message;
        private int offset;
        private int len;

        public OutMessage(byte[] message, int offset, int len) {
            this.message = message;
            this.offset = offset;
            this.len = len;
        }

        public int getOffset() {
            return offset;
        }

        public int getLen() {
            return len;
        }

        public byte[] getMessage() {
            return message;
        }
    }

    public static class InMessage {
        private byte[] data;
        private int offset;
        private int len;

        public InMessage(byte[] data, int offset, int len) {
            this.data = data;
            this.offset = offset;
            this.len = len;
        }

        public byte[] getData() {
            return data;
        }

        public int getOffset() {
            return offset;
        }

        public int getLen() {
            return len;
        }
    }

    public static class NetworkChanged {
        private int state;
        public NetworkChanged(int state) {
            this.state = state;
        }
    }

    public static class ForceNetworkCheck {}

    public static class PerformConnectionCheck {
        boolean ignoreNetworkState = false;
        public PerformConnectionCheck(){
        }
        public PerformConnectionCheck(boolean ignoreNetworkState){
            this.ignoreNetworkState = ignoreNetworkState;
        }
    }

    private static class ConnectionDie {
        private int connectionId;

        public ConnectionDie(int connectionId) {
            this.connectionId = connectionId;
        }

        public int getConnectionId() {
            return connectionId;
        }
    }

    private static class ConnectionCreateFailure {

    }

    private static class ConnectionCreated {
        private int connectionId;
        private Connection connection;

        public ConnectionCreated(int connectionId, Connection connection) {
            this.connectionId = connectionId;
            this.connection = connection;
        }

        public int getConnectionId() {
            return connectionId;
        }

        public Connection getConnection() {
            return connection;
        }
    }
}
