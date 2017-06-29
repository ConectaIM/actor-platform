package im.actor.core.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import im.actor.core.api.ApiUserOutPeer;
import im.actor.core.network.parser.Request;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class RequestLoadPublicKey extends Request<ResponsePublicKeys> {

    public static final int HEADER = 0xa2d;

    public static RequestLoadPublicKey fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestLoadPublicKey(), data);
    }

    private ApiUserOutPeer userPeer;
    private int keyGroupId;
    private List<Long> keyIds;

    public RequestLoadPublicKey(@NotNull ApiUserOutPeer userPeer, int keyGroupId, @NotNull List<Long> keyIds) {
        this.userPeer = userPeer;
        this.keyGroupId = keyGroupId;
        this.keyIds = keyIds;
    }

    public RequestLoadPublicKey() {

    }

    @NotNull
    public ApiUserOutPeer getUserPeer() {
        return this.userPeer;
    }

    public int getKeyGroupId() {
        return this.keyGroupId;
    }

    @NotNull
    public List<Long> getKeyIds() {
        return this.keyIds;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.userPeer = values.getObj(1, new ApiUserOutPeer());
        this.keyGroupId = values.getInt(2);
        this.keyIds = values.getRepeatedLong(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.userPeer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.userPeer);
        writer.writeInt(2, this.keyGroupId);
        writer.writeRepeatedLong(3, this.keyIds);
    }

    @Override
    public String toString() {
        String res = "rpc LoadPublicKey{";
        res += "userPeer=" + this.userPeer;
        res += ", keyGroupId=" + this.keyGroupId;
        res += ", keyIds=" + this.keyIds;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
