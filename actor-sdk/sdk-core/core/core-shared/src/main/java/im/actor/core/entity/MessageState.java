/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.core.entity;

public final class MessageState {

    public static final int PENDING = 1;
    public static final int SENT = 2;
    public static final int ERROR = 5;
    public static final int UNKNOWN = 6;

    public static int fromValue(int value) {
        switch (value) {
            case 1:
                return PENDING;
            case 2:
            case 3:
            case 4:
                return SENT;
            case 5:
                return ERROR;
            default:
            case 6:
                return UNKNOWN;
        }
    }
}
