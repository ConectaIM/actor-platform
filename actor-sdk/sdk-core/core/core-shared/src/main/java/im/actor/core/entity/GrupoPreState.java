package im.actor.core.entity;

import java.io.IOException;

import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserCreator;
import im.actor.runtime.bser.BserObject;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;
import im.actor.runtime.mvvm.ValueDefaultCreator;
import im.actor.runtime.storage.KeyValueItem;

/**
 * Created by diego on 27/10/17.
 */

public class GrupoPreState extends BserObject implements KeyValueItem {

    public static GrupoPreState fromBytes(byte[] data) throws IOException {
        return Bser.parse(new GrupoPreState(), data);
    }

    public static BserCreator<GrupoPreState> CREATOR = GrupoPreState::new;

    public static ValueDefaultCreator<GrupoPreState> DEFAULT_CREATOR = idGrupo ->
            new GrupoPreState(idGrupo, false, false);

    public static final String ENTITY_NAME = "GrupoPreState";

    private long idGrupo;
    private boolean isLoaded;
    private boolean isEmpty;

    private GrupoPreState() {
    }

    public GrupoPreState(long idGrupo, boolean isLoaded, boolean isEmpty) {
        this.idGrupo = idGrupo;
        this.isLoaded = isLoaded;
        this.isEmpty = isEmpty;
    }

    public long getIdGrupo() {
        return idGrupo;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        idGrupo = values.getLong(1, -1);
        isLoaded = values.getBool(2, false);
        isEmpty = values.getBool(8, false);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeLong(1, idGrupo);
        writer.writeBool(2, isLoaded);
        writer.writeBool(8, isEmpty);
    }

    @Override
    public long getEngineId() {
        return idGrupo;
    }
}
