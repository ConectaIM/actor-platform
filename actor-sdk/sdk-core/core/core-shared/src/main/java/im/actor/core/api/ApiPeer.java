package im.actor.core.api;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import im.actor.runtime.bser.BserObject;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class ApiPeer extends BserObject {

    private ApiPeerType type;
    private int id;

    public ApiPeer(@NotNull ApiPeerType type, int id) {
        this.type = type;
        this.id = id;
    }

    public ApiPeer() {

    }

    @NotNull
    public ApiPeerType getType() {
        return this.type;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.type = ApiPeerType.parse(values.getInt(1));
        this.id = values.getInt(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.type == null) {
            throw new IOException();
        }
        writer.writeInt(1, this.type.getValue());
        writer.writeInt(2, this.id);
    }

    @Override
    public String toString() {
        String res = "struct Peer{";
        res += "type=" + this.type;
        res += ", id=" + this.id;
        res += "}";
        return res;
    }

}
