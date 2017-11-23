package im.actor.core.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import im.actor.core.api.ApiFileLocation;
import im.actor.core.network.parser.Request;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class RequestGetFileUrls extends Request<ResponseGetFileUrls> {

    public static final int HEADER = 0xa0d;
    public static RequestGetFileUrls fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestGetFileUrls(), data);
    }

    private List<ApiFileLocation> files;

    public RequestGetFileUrls(@NotNull List<ApiFileLocation> files) {
        this.files = files;
    }

    public RequestGetFileUrls() {

    }

    @NotNull
    public List<ApiFileLocation> getFiles() {
        return this.files;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        List<ApiFileLocation> _files = new ArrayList<ApiFileLocation>();
        for (int i = 0; i < values.getRepeatedCount(1); i ++) {
            _files.add(new ApiFileLocation());
        }
        this.files = values.getRepeatedObj(1, _files);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.files);
    }

    @Override
    public String toString() {
        String res = "rpc GetFileUrls{";
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
