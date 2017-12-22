package im.actor.core.entity;

import com.google.j2objc.annotations.Property;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserCreator;
import im.actor.runtime.bser.BserObject;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;
import im.actor.runtime.storage.ListEngineItem;

/**
 * Created by diego on 28/05/17.
 */

public class GroupPre extends BserObject implements ListEngineItem {

    public static Integer DEFAULT_ID = 0;

    public static GroupPre fromBytes(byte[] data) throws IOException {
        return Bser.parse(new GroupPre(), data);
    }

    public static BserCreator<GroupPre> CREATOR = GroupPre::new;
    public static final String ENTITY_NAME = "GroupPre";

    @NotNull
    @SuppressWarnings("NullableProblems")
    @Property("readonly, nonatomic")
    private Integer groupId;

    @NotNull
    @SuppressWarnings("NullableProblems")
    @Property("readonly, nonatomic")
    private Integer parentId;

    @NotNull
    @SuppressWarnings("NullableProblems")
    @Property("readonly, nonatomic")
    private Integer sortOrder;

    @NotNull
    @SuppressWarnings("NullableProblems")
    @Property("readonly, nonatomic")
    private Boolean hasChildren;

    public GroupPre(@NotNull Integer groupId, @NotNull Integer parentId, @NotNull Integer sortOrder, @NotNull Boolean hasChildren) {
        this.groupId = groupId;
        this.parentId = parentId;
        this.sortOrder = sortOrder;
        this.hasChildren = hasChildren;
    }

    public GroupPre(@NotNull Integer groupId, @NotNull Integer parentId) {
        this.groupId = groupId;
        this.parentId = parentId;
        this.sortOrder = 0;
        this.hasChildren = false;
    }

    public GroupPre(@NotNull Integer groupId) {
        this.groupId = groupId;
        this.parentId = DEFAULT_ID;
        this.sortOrder = 0;
        this.hasChildren = false;
    }

    private GroupPre(){
        super();
    }

    @NotNull
    public Integer getGroupId() {
        return groupId;
    }

    @NotNull
    public Integer getParentId() {
        return parentId;
    }

    @NotNull
    public Integer getSortOrder() {
        return sortOrder;
    }

    @NotNull
    public Boolean getHasChildren() {
        return hasChildren;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        groupId = values.getInt(1);
        parentId = values.getInt(2);
        sortOrder = values.getInt(3);
        hasChildren = values.getBool(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, groupId);
        writer.writeInt(2, parentId);
        writer.writeInt(3, sortOrder);
        writer.writeBool(4, hasChildren);
    }

    @Override
    public long getEngineId() {
        return this.groupId;
    }

    @Override
    public long getEngineSort() {
        return sortOrder;
    }

    @Override
    public String getEngineSearch() {
        return "";
    }
}
