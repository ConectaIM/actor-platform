package im.actor.sdk.controllers.contacts;

import android.os.Bundle;

import im.actor.sdk.R;
import im.actor.sdk.controllers.activity.BaseFragmentActivity;

public class InviteActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.contacts_invite_via_link);


        if (savedInstanceState == null) {
            showFragment(new InviteFragment(), false);
        }
    }
}
