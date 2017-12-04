package im.actor.sdk.controllers.grouppre;

import android.os.Bundle;

import im.actor.sdk.R;
import im.actor.sdk.controllers.activity.BaseFragmentActivity;
import im.actor.sdk.controllers.auth.PickCountryFragment;

/**
 * Created by diego on 03/12/17.
 */

public class SelectGroupPreParentActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.select_group_parent);

        if (savedInstanceState == null) {
            showFragment(new PickCountryFragment(), false);
        }
    }

}
