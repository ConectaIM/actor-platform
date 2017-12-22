package im.actor.sdk.controllers.grouppre.admin;

import im.actor.sdk.controllers.activity.BaseFragmentActivity;

import android.os.Bundle;

import im.actor.sdk.controllers.Intents;
/**
 * Created by dsilv on 18/11/2017.
 */

public class GroupPreSelectParentActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            showFragment(GroupPreSelectParentFragment2.create(
                    getIntent().getIntExtra(Intents.EXTRA_GROUP_ID, 0)), false);
        }
    }

}
