/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.core.entity;

import com.google.j2objc.annotations.Property;

import java.io.IOException;

import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserCreator;
import im.actor.runtime.bser.BserObject;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

import static im.actor.core.entity.PeerType.GROUP;
import static im.actor.core.entity.PeerType.PRIVATE;
import static im.actor.core.entity.PeerType.PRIVATE_ENCRYPTED;

public class Peer extends BserObject {

    public static final BserCreator<Peer> CREATOR = new BserCreator<Peer>() {
        @Override
        public Peer createInstance() {
            return new Peer();
        }
    };

    public static Peer fromBytes(byte[] data) throws IOException {
        return Bser.parse(new Peer(), data);
    }

    public static Peer fromUniqueId(long uid) {
        int id = (int) (uid & 0xFFFFFFFFL);
        int type = (int) ((uid >> 32) & 0xFFFFFFFFL);

        switch (type) {
            default:
            case 0:
                return new Peer(PRIVATE, id);
            case 1:
                return new Peer(GROUP, id);
        }
    }

    public static Peer user(int uid) {
        return new Peer(PRIVATE, uid);
    }

    public static Peer group(int gid) {
        return new Peer(GROUP, gid);
    }

    @Property("readonly, nonatomic")
    private int peerType;
    @Property("readonly, nonatomic")
    private int peerId;

    public Peer(int peerType, int peerId) {
        this.peerType = peerType;
        this.peerId = peerId;
    }

    private Peer() {

    }

    public long getUnuqueId() {
        int type;
        switch (peerType) {
            default:
            case PRIVATE:
                type = 0;
                break;
            case GROUP:
                type = 1;
                break;
        }
        return ((long) peerId & 0xFFFFFFFFL) + (((long) type & 0xFFFFFFFFL) << 32);
    }

    public int getPeerType() {
        return peerType;
    }

    public int getPeerId() {
        return peerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Peer peer = (Peer) o;

        if (peerId != peer.peerId) return false;
        if (peerType != peer.peerType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = peerType;
        result = 31 * result + peerId;
        return result;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        peerId = values.getInt(1);
        switch (values.getInt(2)) {
            default:
            case 1:
                peerType = PRIVATE;
                break;
            case 3:
                peerType = GROUP;
                break;
            case 4:
                peerType = PRIVATE_ENCRYPTED;
                break;
        }
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, peerId);
        switch (peerType) {
            default:
            case PRIVATE:
                writer.writeInt(2, 1);
                break;
            case GROUP:
                writer.writeInt(2, 3);
                break;
            case PRIVATE_ENCRYPTED:
                writer.writeInt(2, 4);
                break;
        }
    }

    @Override
    public String toString() {
        return "{type:" + peerType + ", id:" + peerId + "}";
    }

    public String toIdString() {
        return peerType + "_" + peerId;
    }
}
