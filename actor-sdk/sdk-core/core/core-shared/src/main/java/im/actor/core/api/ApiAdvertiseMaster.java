package im.actor.core.api;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class ApiAdvertiseMaster extends ApiWebRTCSignaling {

    private List<ApiICEServer> server;

    public ApiAdvertiseMaster(@NotNull List<ApiICEServer> server) {
        this.server = server;
    }

    public ApiAdvertiseMaster() {

    }

    public int getHeader() {
        return 26;
    }

    @NotNull
    public List<ApiICEServer> getServer() {
        return this.server;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        List<ApiICEServer> _server = new ArrayList<ApiICEServer>();
        for (int i = 0; i < values.getRepeatedCount(1); i ++) {
            _server.add(new ApiICEServer());
        }
        this.server = values.getRepeatedObj(1, _server);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.server);
    }

    @Override
    public String toString() {
        String res = "struct AdvertiseMaster{";
        res += "}";
        return res;
    }

}
