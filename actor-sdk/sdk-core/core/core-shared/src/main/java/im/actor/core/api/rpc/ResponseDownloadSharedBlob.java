package im.actor.core.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import im.actor.core.network.parser.Response;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class ResponseDownloadSharedBlob extends Response {

    public static final int HEADER = 0xa67;
    public static ResponseDownloadSharedBlob fromBytes(byte[] data) throws IOException {
        return Bser.parse(new ResponseDownloadSharedBlob(), data);
    }

    private byte[] blob;

    public ResponseDownloadSharedBlob(@NotNull byte[] blob) {
        this.blob = blob;
    }

    public ResponseDownloadSharedBlob() {

    }

    @NotNull
    public byte[] getBlob() {
        return this.blob;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.blob = values.getBytes(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.blob == null) {
            throw new IOException();
        }
        writer.writeBytes(1, this.blob);
    }

    @Override
    public String toString() {
        String res = "tuple DownloadSharedBlob{";
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
