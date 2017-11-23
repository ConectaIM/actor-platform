package im.actor.core.api.updates;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import java.io.IOException;

import im.actor.core.network.parser.Update;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class UpdateGroupUserLeaveObsolete extends Update {

    public static final int HEADER = 0x17;
    public static UpdateGroupUserLeaveObsolete fromBytes(byte[] data) throws IOException {
        return Bser.parse(new UpdateGroupUserLeaveObsolete(), data);
    }

    private int groupId;
    private long rid;
    private int uid;
    private long date;

    public UpdateGroupUserLeaveObsolete(int groupId, long rid, int uid, long date) {
        this.groupId = groupId;
        this.rid = rid;
        this.uid = uid;
        this.date = date;
    }

    public UpdateGroupUserLeaveObsolete() {

    }

    public int getGroupId() {
        return this.groupId;
    }

    public long getRid() {
        return this.rid;
    }

    public int getUid() {
        return this.uid;
    }

    public long getDate() {
        return this.date;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupId = values.getInt(1);
        this.rid = values.getLong(4);
        this.uid = values.getInt(2);
        this.date = values.getLong(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.groupId);
        writer.writeLong(4, this.rid);
        writer.writeInt(2, this.uid);
        writer.writeLong(3, this.date);
    }

    @Override
    public String toString() {
        String res = "update GroupUserLeaveObsolete{";
        res += "groupId=" + this.groupId;
        res += ", rid=" + this.rid;
        res += ", uid=" + this.uid;
        res += ", date=" + this.date;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
