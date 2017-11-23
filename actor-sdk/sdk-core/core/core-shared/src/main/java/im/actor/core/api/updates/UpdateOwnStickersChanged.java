package im.actor.core.api.updates;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import im.actor.core.api.ApiStickerCollection;
import im.actor.core.network.parser.Update;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class UpdateOwnStickersChanged extends Update {

    public static final int HEADER = 0xa1;
    public static UpdateOwnStickersChanged fromBytes(byte[] data) throws IOException {
        return Bser.parse(new UpdateOwnStickersChanged(), data);
    }

    private List<ApiStickerCollection> collections;

    public UpdateOwnStickersChanged(@NotNull List<ApiStickerCollection> collections) {
        this.collections = collections;
    }

    public UpdateOwnStickersChanged() {

    }

    @NotNull
    public List<ApiStickerCollection> getCollections() {
        return this.collections;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        List<ApiStickerCollection> _collections = new ArrayList<ApiStickerCollection>();
        for (int i = 0; i < values.getRepeatedCount(1); i ++) {
            _collections.add(new ApiStickerCollection());
        }
        this.collections = values.getRepeatedObj(1, _collections);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.collections);
    }

    @Override
    public String toString() {
        String res = "update OwnStickersChanged{";
        res += "collections=" + this.collections.size();
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
