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

public class RequestLoadDialogs extends Request<ResponseLoadDialogs> {

    public static final int HEADER = 0x68;
    public static RequestLoadDialogs fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestLoadDialogs(), data);
    }

    private long minDate;
    private int limit;
    private List<ApiUpdateOptimization> optimizations;

    public RequestLoadDialogs(long minDate, int limit, @NotNull List<ApiUpdateOptimization> optimizations) {
        this.minDate = minDate;
        this.limit = limit;
        this.optimizations = optimizations;
    }

    public RequestLoadDialogs() {

    }

    public long getMinDate() {
        return this.minDate;
    }

    public int getLimit() {
        return this.limit;
    }

    @NotNull
    public List<ApiUpdateOptimization> getOptimizations() {
        return this.optimizations;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.minDate = values.getLong(1);
        this.limit = values.getInt(2);
        this.optimizations = new ArrayList<ApiUpdateOptimization>();
        for (int b : values.getRepeatedInt(3)) {
            optimizations.add(ApiUpdateOptimization.parse(b));
        }
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeLong(1, this.minDate);
        writer.writeInt(2, this.limit);
        for (ApiUpdateOptimization i : this.optimizations) {
            writer.writeInt(3, i.getValue());
        }
    }

    @Override
    public String toString() {
        String res = "rpc LoadDialogs{";
        res += "minDate=" + this.minDate;
        res += ", limit=" + this.limit;
        res += ", optimizations=" + this.optimizations;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
