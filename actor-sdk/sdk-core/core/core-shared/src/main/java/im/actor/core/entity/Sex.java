/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.core.entity;

import org.jetbrains.annotations.NotNull;

import im.actor.core.api.ApiSex;

public abstract class Sex {
    public static final int UNKNOWN = 1;
    public static final int MALE = 2;
    public static final int FEMALE = 3;

    @NotNull
    public static int fromValue(int value) {
        switch (value) {
            default:
            case 1:
                return UNKNOWN;
            case 2:
                return MALE;
            case 3:
                return FEMALE;
        }
    }

    public static ApiSex toApi(int sex) {
        switch (sex) {
            case FEMALE:
                return ApiSex.FEMALE;
            case MALE:
                return ApiSex.MALE;
            default:
                return ApiSex.UNKNOWN;
        }
    }
}
