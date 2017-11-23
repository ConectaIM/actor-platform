package im.actor.core.api.updates;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import im.actor.core.api.ApiSyncedValue;
import im.actor.core.network.parser.Update;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class UpdateSynedSetUpdated extends Update {

    public static final int HEADER = 0x48;
    public static UpdateSynedSetUpdated fromBytes(byte[] data) throws IOException {
        return Bser.parse(new UpdateSynedSetUpdated(), data);
    }

    private String setName;
    private List<ApiSyncedValue> syncedValues;
    private Boolean isStrong;

    public UpdateSynedSetUpdated(@NotNull String setName, @NotNull List<ApiSyncedValue> syncedValues, @Nullable Boolean isStrong) {
        this.setName = setName;
        this.syncedValues = syncedValues;
        this.isStrong = isStrong;
    }

    public UpdateSynedSetUpdated() {

    }

    @NotNull
    public String getSetName() {
        return this.setName;
    }

    @NotNull
    public List<ApiSyncedValue> getSyncedValues() {
        return this.syncedValues;
    }

    @Nullable
    public Boolean isStrong() {
        return this.isStrong;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.setName = values.getString(1);
        List<ApiSyncedValue> _syncedValues = new ArrayList<ApiSyncedValue>();
        for (int i = 0; i < values.getRepeatedCount(2); i ++) {
            _syncedValues.add(new ApiSyncedValue());
        }
        this.syncedValues = values.getRepeatedObj(2, _syncedValues);
        this.isStrong = values.optBool(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.setName == null) {
            throw new IOException();
        }
        writer.writeString(1, this.setName);
        writer.writeRepeatedObj(2, this.syncedValues);
        if (this.isStrong != null) {
            writer.writeBool(3, this.isStrong);
        }
    }

    @Override
    public String toString() {
        String res = "update SynedSetUpdated{";
        res += "setName=" + this.setName;
        res += ", syncedValues=" + this.syncedValues;
        res += ", isStrong=" + this.isStrong;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
