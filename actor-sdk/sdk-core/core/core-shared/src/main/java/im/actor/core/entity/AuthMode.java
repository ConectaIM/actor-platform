package im.actor.core.entity;

import im.actor.core.api.ApiEmailActivationType;
import im.actor.core.api.ApiPhoneActivationType;

public abstract class AuthMode {

    public static final int OTP = 0;
    public static final int PASSWORD = 1;
    public static final int OAUTH2 = 2;
    public static final int UNSUPPORTED = 3;

    public static int fromApi(ApiEmailActivationType activationType) {
        switch (activationType) {
            case CODE:
                return OTP;
            case PASSWORD:
                return PASSWORD;
            case OAUTH2:
                return OAUTH2;
            default:
            case UNSUPPORTED_VALUE:
                return UNSUPPORTED;
        }
    }

    public static int fromApi(ApiPhoneActivationType activationType) {
        switch (activationType) {
            case CODE:
                return OTP;
            case PASSWORD:
                return PASSWORD;
            default:
            case UNSUPPORTED_VALUE:
                return UNSUPPORTED;
        }
    }
}
