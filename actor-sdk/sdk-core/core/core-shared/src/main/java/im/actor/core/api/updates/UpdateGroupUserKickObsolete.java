package im.actor.core.api.updates;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import java.io.IOException;

import im.actor.core.network.parser.Update;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class UpdateGroupUserKickObsolete extends Update {

    public static final int HEADER = 0x18;
    public static UpdateGroupUserKickObsolete fromBytes(byte[] data) throws IOException {
        return Bser.parse(new UpdateGroupUserKickObsolete(), data);
    }

    private int groupId;
    private long rid;
    private int uid;
    private int kickerUid;
    private long date;

    public UpdateGroupUserKickObsolete(int groupId, long rid, int uid, int kickerUid, long date) {
        this.groupId = groupId;
        this.rid = rid;
        this.uid = uid;
        this.kickerUid = kickerUid;
        this.date = date;
    }

    public UpdateGroupUserKickObsolete() {

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

    public int getKickerUid() {
        return this.kickerUid;
    }

    public long getDate() {
        return this.date;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupId = values.getInt(1);
        this.rid = values.getLong(5);
        this.uid = values.getInt(2);
        this.kickerUid = values.getInt(3);
        this.date = values.getLong(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.groupId);
        writer.writeLong(5, this.rid);
        writer.writeInt(2, this.uid);
        writer.writeInt(3, this.kickerUid);
        writer.writeLong(4, this.date);
    }

    @Override
    public String toString() {
        String res = "update GroupUserKickObsolete{";
        res += "groupId=" + this.groupId;
        res += ", rid=" + this.rid;
        res += ", uid=" + this.uid;
        res += ", kickerUid=" + this.kickerUid;
        res += ", date=" + this.date;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
