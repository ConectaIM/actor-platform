package im.actor.core.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import java.io.IOException;

import im.actor.core.network.parser.Request;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class RequestConnectKeyGroup extends Request<ResponseVoid> {

    public static final int HEADER = 0xa36;
    public static RequestConnectKeyGroup fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestConnectKeyGroup(), data);
    }

    private int keyGroupId;

    public RequestConnectKeyGroup(int keyGroupId) {
        this.keyGroupId = keyGroupId;
    }

    public RequestConnectKeyGroup() {

    }

    public int getKeyGroupId() {
        return this.keyGroupId;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.keyGroupId = values.getInt(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.keyGroupId);
    }

    @Override
    public String toString() {
        String res = "rpc ConnectKeyGroup{";
        res += "keyGroupId=" + this.keyGroupId;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
