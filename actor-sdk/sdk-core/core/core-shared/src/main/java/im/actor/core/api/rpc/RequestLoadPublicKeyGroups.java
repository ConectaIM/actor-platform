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

public class RequestLoadPublicKeyGroups extends Request<ResponsePublicKeyGroups> {

    public static final int HEADER = 0xa29;
    public static RequestLoadPublicKeyGroups fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestLoadPublicKeyGroups(), data);
    }

    private ApiUserOutPeer userPeer;

    public RequestLoadPublicKeyGroups(@NotNull ApiUserOutPeer userPeer) {
        this.userPeer = userPeer;
    }

    public RequestLoadPublicKeyGroups() {

    }

    @NotNull
    public ApiUserOutPeer getUserPeer() {
        return this.userPeer;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.userPeer = values.getObj(1, new ApiUserOutPeer());
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.userPeer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.userPeer);
    }

    @Override
    public String toString() {
        String res = "rpc LoadPublicKeyGroups{";
        res += "userPeer=" + this.userPeer;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
