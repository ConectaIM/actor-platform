package im.actor.push;

import android.content.Context;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import im.actor.runtime.Log;
import im.actor.runtime.util.ExponentialBackoff;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.core.ActorPushManager;

public class PushManager implements ActorPushManager {
    private static final String TAG = "im.actor.push.PushManager";
    private boolean isRegistered = false;

    @Override
    public void registerPush(final Context context) {

        if (!isRegistered) {

            Log.d(TAG, "Requesting push token...");

            new Thread() {
                @Override
                public void run() {
                    ExponentialBackoff exponentialBackoff = new ExponentialBackoff();
                    while (true) {
                        try {
                            String regId = tryRegisterPush(context);
                            if (regId != null) {
                                Log.d(TAG, "Token loaded");
                                onPushRegistered(regId);
                                return;
                            } else {
                                Log.d(TAG, "Unable to load Token");
                                exponentialBackoff.onFailure();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e);
                            exponentialBackoff.onFailure();
                        }
                        long waitTime = exponentialBackoff.exponentialWait();
                        Log.d(TAG, "Next attempt in " + waitTime + " ms");
                        try {
                            Thread.sleep(waitTime);
                        } catch (InterruptedException e1) {
                            Log.e(TAG, e1);
                            return;
                        }
                    }
                }
            }.start();
        } else {
            Log.d(TAG, "Already registered token");
        }
    }

    private void onPushRegistered(String token) {
        isRegistered = true;
        ActorSDK.sharedActor().getMessenger().getPreferences().putBool("push_registered", true);
        ActorSDK.sharedActor().getMessenger().registerGooglePush(ActorSDK.sharedActor().getPushId(), token);

    }

    private String tryRegisterPush(Context context) {
        Log.d(TAG, "Requesting push token iteration...");
        try {
            String regId = InstanceID.getInstance(context)
                    .getToken(String.valueOf(ActorSDK.sharedActor().getPushId()),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            return "FCM_" + regId;
        } catch (IOException e) {
            Log.e(TAG, e);
            return null;
        }
    }

}
