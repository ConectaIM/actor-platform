package im.actor.sdk.util;

import android.content.Context;
import android.content.Intent;

import im.actor.core.AndroidMessenger;
import im.actor.core.entity.Group;
import im.actor.core.entity.User;
import im.actor.core.viewmodel.GroupVM;
import im.actor.core.viewmodel.UserVM;
import im.actor.runtime.mvvm.MVVMCollection;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.controllers.root.RootActivity;
import im.actor.sdk.intents.ActorIntent;
import im.actor.sdk.intents.ActorIntentActivity;

public class ActorSDKMessenger {

    public static AndroidMessenger messenger() {
        return ActorSDK.sharedActor().getMessenger();
    }

    public static MVVMCollection<User, UserVM> users() {
        ActorSDK.sharedActor().waitForReady();
        return messenger().getUsers();
    }

    public static MVVMCollection<Group, GroupVM> groups() {
        return messenger().getGroups();
    }

    public static int myUid() {
        ActorSDK.sharedActor().waitForReady();
        return messenger().myUid();
    }

    public static void returnToRoot(Context context) {
        Intent i;
        ActorIntent startIntent = ActorSDK.sharedActor().getDelegate().getStartIntent();
        if (startIntent != null && startIntent instanceof ActorIntentActivity) {
            i = ((ActorIntentActivity) startIntent).getIntent();
        } else {
            i = new Intent(context, RootActivity.class);
        }
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }
}
