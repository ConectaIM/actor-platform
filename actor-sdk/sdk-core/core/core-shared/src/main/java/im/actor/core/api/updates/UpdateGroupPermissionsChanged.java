package im.actor.core.api.updates;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import java.io.IOException;

import im.actor.core.network.parser.Update;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class UpdateGroupPermissionsChanged extends Update {

    public static final int HEADER = 0xa67;

    public static UpdateGroupPermissionsChanged fromBytes(byte[] data) throws IOException {
        return Bser.parse(new UpdateGroupPermissionsChanged(), data);
    }

    private int groupId;
    private long permissions;

    public UpdateGroupPermissionsChanged(int groupId, long permissions) {
        this.groupId = groupId;
        this.permissions = permissions;
    }

    public UpdateGroupPermissionsChanged() {

    }

    public int getGroupId() {
        return this.groupId;
    }

    public long getPermissions() {
        return this.permissions;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupId = values.getInt(1);
        this.permissions = values.getLong(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.groupId);
        writer.writeLong(2, this.permissions);
    }

    @Override
    public String toString() {
        String res = "update GroupPermissionsChanged{";
        res += "groupId=" + this.groupId;
        res += ", permissions=" + this.permissions;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
