package im.actor.core.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import im.actor.core.network.parser.Request;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class RequestJoinEventBus extends Request<ResponseJoinEventBus> {

    public static final int HEADER = 0xa6c;
    public static RequestJoinEventBus fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestJoinEventBus(), data);
    }

    private String id;
    private Long timeout;

    public RequestJoinEventBus(@NotNull String id, @Nullable Long timeout) {
        this.id = id;
        this.timeout = timeout;
    }

    public RequestJoinEventBus() {

    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @Nullable
    public Long getTimeout() {
        return this.timeout;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.id = values.getString(1);
        this.timeout = values.optLong(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.id == null) {
            throw new IOException();
        }
        writer.writeString(1, this.id);
        if (this.timeout != null) {
            writer.writeLong(2, this.timeout);
        }
    }

    @Override
    public String toString() {
        String res = "rpc JoinEventBus{";
        res += "id=" + this.id;
        res += ", timeout=" + this.timeout;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
