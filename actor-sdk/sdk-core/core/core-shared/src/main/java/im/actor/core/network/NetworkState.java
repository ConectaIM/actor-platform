/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.core.network;

/**
 * State of current network environment
 */
public class NetworkState {
    public static final int UNKNOWN = 0;
    public static final int MOBILE = 1;
    public static final int WI_FI = 2;
    public static final int NO_CONNECTION = 3;

    public static String getDesription(int state){
        switch (state){
            case UNKNOWN: return "UNKNOWN";
            case MOBILE: return "MOBILE";
            case WI_FI: return "WI_FI";
            case NO_CONNECTION: return "NO_CONNECTION";
            default: return "UNKNOWN";
        }
    }
}
