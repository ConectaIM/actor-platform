package im.actor.core.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import im.actor.core.api.ApiFullUser;
import im.actor.core.network.parser.Response;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class ResponseLoadFullUsers extends Response {

    public static final int HEADER = 0xa5a;
    public static ResponseLoadFullUsers fromBytes(byte[] data) throws IOException {
        return Bser.parse(new ResponseLoadFullUsers(), data);
    }

    private List<ApiFullUser> fullUsers;

    public ResponseLoadFullUsers(@NotNull List<ApiFullUser> fullUsers) {
        this.fullUsers = fullUsers;
    }

    public ResponseLoadFullUsers() {

    }

    @NotNull
    public List<ApiFullUser> getFullUsers() {
        return this.fullUsers;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        List<ApiFullUser> _fullUsers = new ArrayList<ApiFullUser>();
        for (int i = 0; i < values.getRepeatedCount(1); i ++) {
            _fullUsers.add(new ApiFullUser());
        }
        this.fullUsers = values.getRepeatedObj(1, _fullUsers);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.fullUsers);
    }

    @Override
    public String toString() {
        String res = "tuple LoadFullUsers{";
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
