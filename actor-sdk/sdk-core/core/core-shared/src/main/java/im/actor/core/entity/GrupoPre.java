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

public class GrupoPre extends BserObject implements ListEngineItem {

    public static GrupoPre fromBytes(byte[] data) throws IOException {
        return Bser.parse(new GrupoPre(), data);
    }

    public static BserCreator<GrupoPre> CREATOR = () -> new GrupoPre();

    public static final String ENTITY_NAME = "GrupoPre";
    private static final int MAX_LENGTH = 32;

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

    public GrupoPre(@NotNull Group group, @NotNull Integer ordem, @NotNull Boolean hasChildren) {
        this.group = group;
        this.ordem = ordem;
        this.hasChildren = hasChildren;
    }

    private GrupoPre(){

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
        group = Group.fromBytes(values.getBytes(1));
        ordem = values.getInt(2);
        hasChildren = values.getBool(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeObject(1, group);
        writer.writeInt(2, ordem);
        writer.writeBool(3, hasChildren);
    }

    @Override
    public long getEngineId() {
        return group.getEngineId();
    }

    @Override
    public long getEngineSort() {
        return ordem;
    }

    @Override
    public String getEngineSearch() {
        return getGroup().getTitle();
    }
}
