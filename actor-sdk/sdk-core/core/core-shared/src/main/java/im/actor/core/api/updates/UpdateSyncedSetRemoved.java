package im.actor.core.api.updates;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import im.actor.core.network.parser.Update;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class UpdateSyncedSetRemoved extends Update {

    public static final int HEADER = 0x4a;

    public static UpdateSyncedSetRemoved fromBytes(byte[] data) throws IOException {
        return Bser.parse(new UpdateSyncedSetRemoved(), data);
    }

    private String setName;
    private List<Long> removedItems;

    public UpdateSyncedSetRemoved(@NotNull String setName, @NotNull List<Long> removedItems) {
        this.setName = setName;
        this.removedItems = removedItems;
    }

    public UpdateSyncedSetRemoved() {

    }

    @NotNull
    public String getSetName() {
        return this.setName;
    }

    @NotNull
    public List<Long> getRemovedItems() {
        return this.removedItems;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.setName = values.getString(1);
        this.removedItems = values.getRepeatedLong(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.setName == null) {
            throw new IOException();
        }
        writer.writeString(1, this.setName);
        writer.writeRepeatedLong(2, this.removedItems);
    }

    @Override
    public String toString() {
        String res = "update SyncedSetRemoved{";
        res += "setName=" + this.setName;
        res += ", removedItems=" + this.removedItems;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
