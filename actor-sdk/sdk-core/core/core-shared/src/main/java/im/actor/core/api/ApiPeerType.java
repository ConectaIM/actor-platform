package im.actor.core.api;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import java.io.IOException;

public enum ApiPeerType {

    PRIVATE(1),
    GROUP(2),
    ENCRYPTEDPRIVATE(3),
    UNSUPPORTED_VALUE(-1);

    private int value;

    ApiPeerType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ApiPeerType parse(int value) throws IOException {
        switch (value) {
            case 1:
                return ApiPeerType.PRIVATE;
            case 2:
                return ApiPeerType.GROUP;
            case 3:
                return ApiPeerType.ENCRYPTEDPRIVATE;
            default:
                return ApiPeerType.UNSUPPORTED_VALUE;
        }
    }
}
