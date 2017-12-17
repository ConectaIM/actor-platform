package im.actor.core.entity;

import java.io.IOException;

import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserCreator;
import im.actor.runtime.bser.BserObject;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;
import im.actor.runtime.mvvm.ValueDefaultCreator;
import im.actor.runtime.storage.KeyValueItem;

public class GroupPreState extends BserObject implements KeyValueItem {

    public static GroupPreState fromBytes(byte[] data) throws IOException {
        return Bser.parse(new GroupPreState(), data);
    }

    public static BserCreator<GroupPreState> CREATOR = GroupPreState::new;

    public static ValueDefaultCreator<GroupPreState> DEFAULT_CREATOR = groupId ->
            new GroupPreState(groupId, GroupPre.DEFAULT_ID, false);

    public static final String ENTITY_NAME = "GroupPreState";

    private long groupId;
    private int parentId;
    private boolean isLoaded;

    public GroupPreState(long groupId, int parentId, boolean isLoaded) {
        this.groupId = groupId;
        this.parentId = parentId;
        this.isLoaded = isLoaded;
    }

    private GroupPreState() {

    }

    public GroupPreState changeIsLoaded(boolean isLoaded) {
        return new GroupPreState(groupId, parentId, isLoaded);
    }

    public long getGroupId() {
        return groupId;
    }

    public int getParentId() {
        return parentId;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        groupId = values.getLong(1);
        parentId = values.getInt(2);
        isLoaded = values.getBool(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeLong(1, groupId);
        writer.writeInt(2, parentId);
        writer.writeBool(3, isLoaded);
    }

    @Override
    public long getEngineId() {
        return groupId;
    }
}
