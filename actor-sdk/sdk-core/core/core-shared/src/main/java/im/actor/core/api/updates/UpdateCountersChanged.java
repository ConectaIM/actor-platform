package im.actor.core.api.updates;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import im.actor.core.api.ApiAppCounters;
import im.actor.core.network.parser.Update;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class UpdateCountersChanged extends Update {

    public static final int HEADER = 0xd7;
    public static UpdateCountersChanged fromBytes(byte[] data) throws IOException {
        return Bser.parse(new UpdateCountersChanged(), data);
    }

    private ApiAppCounters counters;

    public UpdateCountersChanged(@NotNull ApiAppCounters counters) {
        this.counters = counters;
    }

    public UpdateCountersChanged() {

    }

    @NotNull
    public ApiAppCounters getCounters() {
        return this.counters;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.counters = values.getObj(1, new ApiAppCounters());
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.counters == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.counters);
    }

    @Override
    public String toString() {
        String res = "update CountersChanged{";
        res += "counters=" + this.counters;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
