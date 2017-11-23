package im.actor.core.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import im.actor.core.network.parser.Request;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class RequestCreateNewEventBus extends Request<ResponseCreateNewEventBus> {

    public static final int HEADER = 0xa69;
    public static RequestCreateNewEventBus fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestCreateNewEventBus(), data);
    }

    private Long timeout;
    private Boolean isOwned;

    public RequestCreateNewEventBus(@Nullable Long timeout, @Nullable Boolean isOwned) {
        this.timeout = timeout;
        this.isOwned = isOwned;
    }

    public RequestCreateNewEventBus() {

    }

    @Nullable
    public Long getTimeout() {
        return this.timeout;
    }

    @Nullable
    public Boolean isOwned() {
        return this.isOwned;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.timeout = values.optLong(1);
        this.isOwned = values.optBool(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.timeout != null) {
            writer.writeLong(1, this.timeout);
        }
        if (this.isOwned != null) {
            writer.writeBool(2, this.isOwned);
        }
    }

    @Override
    public String toString() {
        String res = "rpc CreateNewEventBus{";
        res += "timeout=" + this.timeout;
        res += ", isOwned=" + this.isOwned;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
