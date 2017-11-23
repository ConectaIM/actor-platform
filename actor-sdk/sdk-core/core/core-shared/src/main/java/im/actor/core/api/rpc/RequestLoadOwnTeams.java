package im.actor.core.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import java.io.IOException;

import im.actor.core.network.parser.Request;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class RequestLoadOwnTeams extends Request<ResponseTeamsList> {

    public static final int HEADER = 0xa01;
    public static RequestLoadOwnTeams fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestLoadOwnTeams(), data);
    }


    public RequestLoadOwnTeams() {

    }

    @Override
    public void parse(BserValues values) throws IOException {
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
    }

    @Override
    public String toString() {
        String res = "rpc LoadOwnTeams{";
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
