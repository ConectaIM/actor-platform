package im.actor.core.api.updates;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import im.actor.core.api.ApiDeviceType;
import im.actor.core.network.parser.Update;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class UpdateUserOffline extends Update {

    public static final int HEADER = 0x8;

    public static UpdateUserOffline fromBytes(byte[] data) throws IOException {
        return Bser.parse(new UpdateUserOffline(), data);
    }

    private int uid;
    private ApiDeviceType deviceType;
    private String deviceCategory;

    public UpdateUserOffline(int uid, @Nullable ApiDeviceType deviceType, @Nullable String deviceCategory) {
        this.uid = uid;
        this.deviceType = deviceType;
        this.deviceCategory = deviceCategory;
    }

    public UpdateUserOffline() {

    }

    public int getUid() {
        return this.uid;
    }

    @Nullable
    public ApiDeviceType getDeviceType() {
        return this.deviceType;
    }

    @Nullable
    public String getDeviceCategory() {
        return this.deviceCategory;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uid = values.getInt(1);
        int val_deviceType = values.getInt(2, 0);
        if (val_deviceType != 0) {
            this.deviceType = ApiDeviceType.parse(val_deviceType);
        }
        this.deviceCategory = values.optString(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.uid);
        if (this.deviceType != null) {
            writer.writeInt(2, this.deviceType.getValue());
        }
        if (this.deviceCategory != null) {
            writer.writeString(3, this.deviceCategory);
        }
    }

    @Override
    public String toString() {
        String res = "update UserOffline{";
        res += "uid=" + this.uid;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
