package im.actor.core.api.updates;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import im.actor.core.api.ApiMember;
import im.actor.core.network.parser.Update;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class UpdateGroupMembersUpdateObsolete extends Update {

    public static final int HEADER = 0x2c;
    public static UpdateGroupMembersUpdateObsolete fromBytes(byte[] data) throws IOException {
        return Bser.parse(new UpdateGroupMembersUpdateObsolete(), data);
    }

    private int groupId;
    private List<ApiMember> members;

    public UpdateGroupMembersUpdateObsolete(int groupId, @NotNull List<ApiMember> members) {
        this.groupId = groupId;
        this.members = members;
    }

    public UpdateGroupMembersUpdateObsolete() {

    }

    public int getGroupId() {
        return this.groupId;
    }

    @NotNull
    public List<ApiMember> getMembers() {
        return this.members;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupId = values.getInt(1);
        List<ApiMember> _members = new ArrayList<ApiMember>();
        for (int i = 0; i < values.getRepeatedCount(2); i ++) {
            _members.add(new ApiMember());
        }
        this.members = values.getRepeatedObj(2, _members);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.groupId);
        writer.writeRepeatedObj(2, this.members);
    }

    @Override
    public String toString() {
        String res = "update GroupMembersUpdateObsolete{";
        res += "groupId=" + this.groupId;
        res += ", members=" + this.members;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
