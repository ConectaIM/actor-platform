package im.actor.core.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import im.actor.core.api.ApiUserOutPeer;
import im.actor.core.network.parser.Request;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class RequestLoadPrePublicKeys extends Request<ResponsePublicKeys> {

    public static final int HEADER = 0xa2b;
    public static RequestLoadPrePublicKeys fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestLoadPrePublicKeys(), data);
    }

    private ApiUserOutPeer userPeer;
    private int keyGroupId;

    public RequestLoadPrePublicKeys(@NotNull ApiUserOutPeer userPeer, int keyGroupId) {
        this.userPeer = userPeer;
        this.keyGroupId = keyGroupId;
    }

    public RequestLoadPrePublicKeys() {

    }

    @NotNull
    public ApiUserOutPeer getUserPeer() {
        return this.userPeer;
    }

    public int getKeyGroupId() {
        return this.keyGroupId;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.userPeer = values.getObj(1, new ApiUserOutPeer());
        this.keyGroupId = values.getInt(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.userPeer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.userPeer);
        writer.writeInt(2, this.keyGroupId);
    }

    @Override
    public String toString() {
        String res = "rpc LoadPrePublicKeys{";
        res += "userPeer=" + this.userPeer;
        res += ", keyGroupId=" + this.keyGroupId;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
