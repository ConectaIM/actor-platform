package im.actor.core.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import im.actor.core.api.ApiOutPeer;
import im.actor.core.network.parser.Request;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class RequestDeleteMessage extends Request<ResponseSeq> {

    public static final int HEADER = 0x62;
    public static RequestDeleteMessage fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestDeleteMessage(), data);
    }

    private ApiOutPeer peer;
    private List<Long> rids;

    public RequestDeleteMessage(@NotNull ApiOutPeer peer, @NotNull List<Long> rids) {
        this.peer = peer;
        this.rids = rids;
    }

    public RequestDeleteMessage() {

    }

    @NotNull
    public ApiOutPeer getPeer() {
        return this.peer;
    }

    @NotNull
    public List<Long> getRids() {
        return this.rids;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, new ApiOutPeer());
        this.rids = values.getRepeatedLong(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeRepeatedLong(3, this.rids);
    }

    @Override
    public String toString() {
        String res = "rpc DeleteMessage{";
        res += "peer=" + this.peer;
        res += ", rids=" + this.rids;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
