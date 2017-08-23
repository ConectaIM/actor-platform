package im.actor.sdk.controllers.docs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;

import im.actor.core.entity.Peer;
import im.actor.sdk.R;
import im.actor.sdk.controllers.activity.BaseActivity;

/**
 * Created by diego on 10/08/17.
 */

public class DocsActivity extends BaseActivity {

    public static final String EXTRA_CHAT_PEER = "chat_peer";

    private Toolbar mToolbar;
    private ViewPager mPager;
    private TabLayout mTabs;
    private AppBarLayout appBarLayout;

    public static Intent build(Peer peer, Context context) {
        final Intent intent = new Intent(context, DocsActivity.class);
        intent.putExtra(EXTRA_CHAT_PEER, peer.getUnuqueId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_media);
        setupTabs();
    }

    private void setupTabs() {
        mPager = (ViewPager) findViewById(R.id.pager);
        TabsPageAdapter pagerAdapter = new TabsPageAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);
    }

    class TabsPageAdapter extends FragmentPagerAdapter {

        private String[] tabText;

        public TabsPageAdapter(FragmentManager fm) {
            super(fm);
            tabText = getResources().getStringArray(R.array.shared_media_tabs);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return getMapaFragment();
                case 1:
                    return getEventosFragment();
                case 2:
                    return getEventosFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getSpanable(position);
                case 1:
                    return getSpanable(position);
            }
            return null;
        }

        public CharSequence getSpanable(int position) {
            SpannableString spannableString = new SpannableString(tabText[position]);
            spannableString.setSpan(spannableString, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
