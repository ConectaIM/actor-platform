package im.actor.core.api.updates;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import java.io.IOException;

import im.actor.core.network.parser.Update;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class UpdateEmptyUpdate extends Update {

    public static final int HEADER = 0x55;
    public static UpdateEmptyUpdate fromBytes(byte[] data) throws IOException {
        return Bser.parse(new UpdateEmptyUpdate(), data);
    }


    public UpdateEmptyUpdate() {

    }

    @Override
    public void parse(BserValues values) throws IOException {
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
    }

    @Override
    public String toString() {
        String res = "update EmptyUpdate{";
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
