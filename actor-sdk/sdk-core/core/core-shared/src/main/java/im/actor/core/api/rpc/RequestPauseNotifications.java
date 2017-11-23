package im.actor.core.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import java.io.IOException;

import im.actor.core.network.parser.Request;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class RequestPauseNotifications extends Request<ResponseVoid> {

    public static final int HEADER = 0xa51;
    public static RequestPauseNotifications fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestPauseNotifications(), data);
    }

    private int timeout;

    public RequestPauseNotifications(int timeout) {
        this.timeout = timeout;
    }

    public RequestPauseNotifications() {

    }

    public int getTimeout() {
        return this.timeout;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.timeout = values.getInt(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.timeout);
    }

    @Override
    public String toString() {
        String res = "rpc PauseNotifications{";
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
