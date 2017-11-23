package im.actor.core.api.updates;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import im.actor.core.network.parser.Update;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class UpdateGroupAboutChangedObsolete extends Update {

    public static final int HEADER = 0xd6;
    public static UpdateGroupAboutChangedObsolete fromBytes(byte[] data) throws IOException {
        return Bser.parse(new UpdateGroupAboutChangedObsolete(), data);
    }

    private int groupId;
    private String about;

    public UpdateGroupAboutChangedObsolete(int groupId, @Nullable String about) {
        this.groupId = groupId;
        this.about = about;
    }

    public UpdateGroupAboutChangedObsolete() {

    }

    public int getGroupId() {
        return this.groupId;
    }

    @Nullable
    public String getAbout() {
        return this.about;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupId = values.getInt(1);
        this.about = values.optString(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.groupId);
        if (this.about != null) {
            writer.writeString(2, this.about);
        }
    }

    @Override
    public String toString() {
        String res = "update GroupAboutChangedObsolete{";
        res += "groupId=" + this.groupId;
        res += ", about=" + this.about;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
