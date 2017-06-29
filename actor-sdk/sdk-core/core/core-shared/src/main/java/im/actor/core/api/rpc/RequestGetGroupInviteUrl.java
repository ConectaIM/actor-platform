package im.actor.core.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import im.actor.core.api.ApiGroupOutPeer;
import im.actor.core.network.parser.Request;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class RequestGetGroupInviteUrl extends Request<ResponseInviteUrl> {

    public static final int HEADER = 0xb1;

    public static RequestGetGroupInviteUrl fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestGetGroupInviteUrl(), data);
    }

    private ApiGroupOutPeer groupPeer;

    public RequestGetGroupInviteUrl(@NotNull ApiGroupOutPeer groupPeer) {
        this.groupPeer = groupPeer;
    }

    public RequestGetGroupInviteUrl() {

    }

    @NotNull
    public ApiGroupOutPeer getGroupPeer() {
        return this.groupPeer;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupPeer = values.getObj(1, new ApiGroupOutPeer());
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.groupPeer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.groupPeer);
    }

    @Override
    public String toString() {
        String res = "rpc GetGroupInviteUrl{";
        res += "groupPeer=" + this.groupPeer;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
