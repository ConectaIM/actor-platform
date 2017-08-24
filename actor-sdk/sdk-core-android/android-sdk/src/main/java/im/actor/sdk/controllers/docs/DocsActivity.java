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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import im.actor.core.entity.Peer;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.ActorStyle;
import im.actor.sdk.R;
import im.actor.sdk.controllers.activity.BaseActivity;
import im.actor.sdk.controllers.conversation.ChatFragment;

/**
 * Created by diego on 10/08/17.
 */

public class DocsActivity extends BaseActivity {

    public static final String EXTRA_CHAT_PEER = "chat_peer";

    private ViewPager mPager;
    private TabLayout tabLayout;
    private Map<Integer, Fragment> fragments = new HashMap<>();
    private Peer peer;
    private ActorStyle style;


    public static Intent build(Peer peer, Context context) {
        final Intent intent = new Intent(context, DocsActivity.class);
        intent.putExtra(EXTRA_CHAT_PEER, peer.getUnuqueId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_media);
        style = ActorSDK.sharedActor().style;
        setupTabs();
        peer = Peer.fromUniqueId(getIntent().getExtras().getLong(EXTRA_CHAT_PEER));

    }

    private void setupTabs() {
        mPager = (ViewPager) findViewById(R.id.pager);
        TabsPageAdapter pagerAdapter = new TabsPageAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(style.getAccentColor());
        tabLayout.setBackgroundColor(style.getMainColor());
        tabLayout.setTabTextColors(style.getTabTextPrimaryColor(), style.getTabTextPrimaryColor());

        tabLayout.setupWithViewPager(mPager);
    }

    private Fragment getFragmentAtPosition(int position){
        if(fragments.containsKey(position)){
            return fragments.get(position);
        }
        Fragment fragment = DocsFragment.create(peer);
        fragments.put(position,fragment);
        return  fragment;
    }


    class TabsPageAdapter extends FragmentPagerAdapter {

        private String[] tabText;

        public TabsPageAdapter(FragmentManager fm) {
            super(fm);
            tabText = getResources().getStringArray(R.array.shared_media_tabs);
        }

        @Override
        public Fragment getItem(int position) {
            return getFragmentAtPosition(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getSpanable(position);
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
