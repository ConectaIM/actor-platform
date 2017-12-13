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

    public static Integer NONE_PARENT_ID = -1;

    public static GroupPre fromBytes(byte[] data) throws IOException {
        return Bser.parse(new GroupPre(), data);
    }

    public static BserCreator<GroupPre> CREATOR = GroupPre::new;
    public static final String ENTITY_NAME = "GroupPre";

    @Property("readonly, nonatomic")
    private int groupId;

    @NotNull
    @SuppressWarnings("NullableProblems")
    @Property("readonly, nonatomic")
    private Group group;

    @NotNull
    @SuppressWarnings("NullableProblems")
    @Property("readonly, nonatomic")
    private Integer ordem;

    @NotNull
    @SuppressWarnings("NullableProblems")
    @Property("readonly, nonatomic")
    private Boolean hasChildren;

    public GroupPre(int groupId, @NotNull Group group, @NotNull Integer ordem, @NotNull Boolean hasChildren) {
        this.groupId = groupId;
        this.group = group;
        this.ordem = ordem;
        this.hasChildren = hasChildren;
    }

    public GroupPre(int groupId, @NotNull Group group) {
        this.groupId = groupId;
        this.group = group;
        this.ordem = 0;
        this.hasChildren = false;
    }

    public GroupPre(int groupId) {
        this.groupId = groupId;
        this.group = null;
        this.ordem = 0;
        this.hasChildren = false;
    }

    private GroupPre(){
        super();
    }

    @NotNull
    public Group getGroup() {
        return group;
    }

    @NotNull
    public Integer getOrdem() {
        return ordem;
    }

    @NotNull
    public Boolean getHasChildren() {
        return hasChildren;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        groupId = values.getInt(1);
        group = Group.fromBytes(values.getBytes(2));
        ordem = values.getInt(3);
        hasChildren = values.getBool(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, groupId);
        writer.writeObject(2, group);
        writer.writeInt(3, ordem);
        writer.writeBool(4, hasChildren);
    }

    @Override
    public long getEngineId() {
        return this.groupId;
    }

    @Override
    public long getEngineSort() {
        return ordem;
    }

    @Override
    public String getEngineSearch() {
        return getGroup() != null ? getGroup().getTitle() : "";
    }
}
