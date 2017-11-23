package im.actor.core.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import im.actor.core.network.parser.Request;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class RequestCreateTeam extends Request<ResponseCreateTeam> {

    public static final int HEADER = 0xa06;
    public static RequestCreateTeam fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestCreateTeam(), data);
    }

    private String title;

    public RequestCreateTeam(@NotNull String title) {
        this.title = title;
    }

    public RequestCreateTeam() {

    }

    @NotNull
    public String getTitle() {
        return this.title;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.title = values.getString(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.title == null) {
            throw new IOException();
        }
        writer.writeString(1, this.title);
    }

    @Override
    public String toString() {
        String res = "rpc CreateTeam{";
        res += "title=" + this.title;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
