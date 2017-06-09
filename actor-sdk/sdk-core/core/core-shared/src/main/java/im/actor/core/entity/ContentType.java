/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.core.entity;

public abstract class ContentType {

    public static final int NONE = 1;
    public static final int TEXT = 2;
    public static final int DOCUMENT = 3;
    public static final int DOCUMENT_PHOTO = 4;
    public static final int DOCUMENT_VIDEO = 5;
    public static final int DOCUMENT_AUDIO = 17;
    public static final int CONTACT = 18;
    public static final int LOCATION = 19;
    public static final int STICKER = 20;
    public static final int SERVICE = 6;
    public static final int SERVICE_ADD = 7;
    public static final int SERVICE_KICK = 8;
    public static final int SERVICE_LEAVE = 9;
    public static final int SERVICE_REGISTERED = 10;
    public static final int SERVICE_CREATED = 11;
    public static final int SERVICE_JOINED = 16;
    public static final int SERVICE_TITLE = 12;
    public static final int SERVICE_AVATAR = 13;
    public static final int SERVICE_AVATAR_REMOVED = 14;
    public static final int CUSTOM_JSON_MESSAGE = 21;
    public static final int SERVICE_CALL_ENDED = 22;
    public static final int SERVICE_CALL_MISSED = 23;
    public static final int SERVICE_TOPIC = 24;
    public static final int SERVICE_ABOUT = 25;
    public static final int UNKNOWN_CONTENT = 15;

    public static int fromValue(int value) {
        switch (value) {
            default:
            case 1:
                return NONE;
            case 2:
                return TEXT;
            case 3:
                return DOCUMENT;
            case 4:
                return DOCUMENT_PHOTO;
            case 5:
                return DOCUMENT_VIDEO;
            case 6:
                return SERVICE;
            case 7:
                return SERVICE_ADD;
            case 8:
                return SERVICE_KICK;
            case 9:
                return SERVICE_LEAVE;
            case 10:
                return SERVICE_REGISTERED;
            case 11:
                return SERVICE_CREATED;
            case 12:
                return SERVICE_TITLE;
            case 13:
                return SERVICE_AVATAR;
            case 14:
                return SERVICE_AVATAR_REMOVED;
            case 16:
                return SERVICE_JOINED;
            case 17:
                return DOCUMENT_AUDIO;
            case 18:
                return CONTACT;
            case 19:
                return LOCATION;
            case 20:
                return STICKER;
            case 21:
                return CUSTOM_JSON_MESSAGE;
            case 22:
                return SERVICE_CALL_ENDED;
            case 23:
                return SERVICE_CALL_MISSED;
            case 24:
                return SERVICE_TOPIC;
            case 25:
                return SERVICE_ABOUT;

        }
    }
}
