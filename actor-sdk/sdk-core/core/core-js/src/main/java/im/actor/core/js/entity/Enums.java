/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.core.js.entity;

import im.actor.core.AuthState;
import im.actor.core.entity.MessageState;

import static im.actor.core.AuthState.AUTH_START;
import static im.actor.core.AuthState.CODE_VALIDATION_EMAIL;
import static im.actor.core.AuthState.CODE_VALIDATION_PHONE;
import static im.actor.core.AuthState.COMPLETE_OAUTH;
import static im.actor.core.AuthState.GET_OAUTH_PARAMS;
import static im.actor.core.AuthState.LOGGED_IN;
import static im.actor.core.AuthState.SIGN_UP;
import static im.actor.core.entity.MessageState.ERROR;
import static im.actor.core.entity.MessageState.PENDING;
import static im.actor.core.entity.MessageState.SENT;
import static im.actor.core.entity.MessageState.UNKNOWN;

public class Enums {
    public static String convertAuthState(int state) {
        switch (state) {
            default:
            case AUTH_START:
                return "start";
            case CODE_VALIDATION_PHONE:
                return "code";
            case CODE_VALIDATION_EMAIL:
                return "code_email";
            case GET_OAUTH_PARAMS:
                return "get_oauth_params";
            case COMPLETE_OAUTH:
                return "complete_oauth";
            case SIGN_UP:
                return "signup";
            case LOGGED_IN:
                return "logged_in";
        }
    }

    public static String convertMessageState(int state) {
        switch (state) {
            default:
            case UNKNOWN:
                return "unknown";
            case PENDING:
                return "pending";
            case SENT:
                return "sent";
            case ERROR:
                return "error";
        }
    }
}
