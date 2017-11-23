package im.actor.core.api;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import im.actor.runtime.bser.BserObject;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class ApiTeam extends BserObject {

    private int id;
    private long accessHash;
    private String name;

    public ApiTeam(int id, long accessHash, @NotNull String name) {
        this.id = id;
        this.accessHash = accessHash;
        this.name = name;
    }

    public ApiTeam() {

    }

    public int getId() {
        return this.id;
    }

    public long getAccessHash() {
        return this.accessHash;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.id = values.getInt(1);
        this.accessHash = values.getLong(2);
        this.name = values.getString(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.id);
        writer.writeLong(2, this.accessHash);
        if (this.name == null) {
            throw new IOException();
        }
        writer.writeString(3, this.name);
    }

    @Override
    public String toString() {
        String res = "struct Team{";
        res += "id=" + this.id;
        res += ", name=" + this.name;
        res += "}";
        return res;
    }

}
