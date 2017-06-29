/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.core;

/**
 * State of Authentication
 */
public interface AuthState {
    int AUTH_START = 0;
    int AUTH_EMAIL = 1;
    int AUTH_PHONE = 2;
    int AUTH_CUSTOM = 3;
    int CODE_VALIDATION_PHONE = 4;
    int CODE_VALIDATION_EMAIL = 5;
    int PASSWORD_VALIDATION = 6;
    int GET_OAUTH_PARAMS = 7;
    int COMPLETE_OAUTH = 8;
    int SIGN_UP = 9;
    int LOGGED_IN = 10;
    int AUTH_NAME = 11;
}
