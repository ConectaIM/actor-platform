package im.actor.core.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import im.actor.core.api.ApiEvent;
import im.actor.core.network.parser.Request;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class RequestStoreEvents extends Request<ResponseVoid> {

    public static final int HEADER = 0xf3;
    public static RequestStoreEvents fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestStoreEvents(), data);
    }

    private List<ApiEvent> events;

    public RequestStoreEvents(@NotNull List<ApiEvent> events) {
        this.events = events;
    }

    public RequestStoreEvents() {

    }

    @NotNull
    public List<ApiEvent> getEvents() {
        return this.events;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.events = new ArrayList<ApiEvent>();
        for (byte[] b : values.getRepeatedBytes(1)) {
            events.add(ApiEvent.fromBytes(b));
        }
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        for (ApiEvent i : this.events) {
            writer.writeBytes(1, i.buildContainer());
        }
    }

    @Override
    public String toString() {
        String res = "rpc StoreEvents{";
        res += "events=" + this.events;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
