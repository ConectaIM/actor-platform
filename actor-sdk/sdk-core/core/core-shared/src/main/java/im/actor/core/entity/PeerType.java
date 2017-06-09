/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.core.entity;

import im.actor.core.api.ApiPeerType;

public abstract class PeerType {

    public static final int PRIVATE = 1;
    public static final int GROUP = 2;
    public static final int PRIVATE_ENCRYPTED = 3;

    public static ApiPeerType toApi(int peerType) {
        switch (peerType) {
            case GROUP:
                return ApiPeerType.GROUP;
            case PRIVATE:
                return ApiPeerType.PRIVATE;
            case PRIVATE_ENCRYPTED:
                return ApiPeerType.ENCRYPTEDPRIVATE;
            default:
                return ApiPeerType.UNSUPPORTED_VALUE;
        }

    }
}
