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
            new GroupPreState(groupId, GroupPre.DEFAULT_ID, false, false);

    public static final String ENTITY_NAME = "GroupPreState";

    private long groupId;
    private int parentId;
    private boolean isLoaded;
    private boolean hasChildren;

    public GroupPreState(long groupId, int parentId, boolean isLoaded, boolean hasChildren) {
        this.groupId = groupId;
        this.parentId = parentId;
        this.isLoaded = isLoaded;
        this.hasChildren = hasChildren;
    }

    private GroupPreState() {

    }

    public GroupPreState changeIsLoaded(boolean isLoaded) {
        return new GroupPreState(groupId, parentId, isLoaded, hasChildren);
    }

    public GroupPreState changeParentId(int parentId) {
        return new GroupPreState(this.groupId, parentId, this.isLoaded, this.hasChildren);
    }

    public long getGroupId() {
        return groupId;
    }

    public int getParentId() {
        return parentId;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        groupId = values.getLong(1);
        parentId = values.getInt(2);
        isLoaded = values.getBool(3);
        hasChildren = values.getBool(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeLong(1, groupId);
        writer.writeInt(2, parentId);
        writer.writeBool(3, isLoaded);
        writer.writeBool(4, hasChildren);
    }

    @Override
    public long getEngineId() {
        return groupId;
    }
}
