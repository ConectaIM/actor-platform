package im.actor.core.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import im.actor.core.api.ApiUpdateOptimization;
import im.actor.core.network.parser.Request;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class RequestJoinGroup extends Request<ResponseJoinGroup> {

    public static final int HEADER = 0xb4;
    public static RequestJoinGroup fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestJoinGroup(), data);
    }

    private String token;
    private List<ApiUpdateOptimization> optimizations;

    public RequestJoinGroup(@NotNull String token, @NotNull List<ApiUpdateOptimization> optimizations) {
        this.token = token;
        this.optimizations = optimizations;
    }

    public RequestJoinGroup() {

    }

    @NotNull
    public String getToken() {
        return this.token;
    }

    @NotNull
    public List<ApiUpdateOptimization> getOptimizations() {
        return this.optimizations;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.token = values.getString(1);
        this.optimizations = new ArrayList<ApiUpdateOptimization>();
        for (int b : values.getRepeatedInt(2)) {
            optimizations.add(ApiUpdateOptimization.parse(b));
        }
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.token == null) {
            throw new IOException();
        }
        writer.writeString(1, this.token);
        for (ApiUpdateOptimization i : this.optimizations) {
            writer.writeInt(2, i.getValue());
        }
    }

    @Override
    public String toString() {
        String res = "rpc JoinGroup{";
        res += "token=" + this.token;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
