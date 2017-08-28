package im.actor.sdk.controllers.docs;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import im.actor.core.entity.Message;
import im.actor.core.entity.Peer;
import im.actor.runtime.android.view.BindedListAdapter;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.util.Screen;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

/**
 * Created by diego on 28/08/17.
 */

public class PhotoFragment extends DocsFragment implements PhotoAdapter.SpanCountListener {

    public static PhotoFragment create(Peer peer) {
        PhotoFragment res = new PhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("peer", peer.getUnuqueId());
        res.setArguments(bundle);
        return res;
    }

    @Override
    protected BindedListAdapter<Message, DocsAdapter.DocsViewHolder> onCreateAdapter(BindedDisplayList<Message> displayList, Activity activity) {
        return new PhotoAdapter(displayList, activity, this);
    }

    @Override
    protected void configureRecyclerView(RecyclerView recyclerView) {

        super.configureRecyclerView(recyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), getSpanCount());
        gridLayoutManager.setRecycleChildrenOnDetach(false);
        gridLayoutManager.setSmoothScrollbarEnabled(false);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ((PhotoAdapter)getAdapter()).onConfigurationChanged(newConfig);
    }


    @Override
    public void onSpanChange(int spanCount) {
        ((GridLayoutManager)getCollection().getLayoutManager()).setSpanCount(spanCount);
    }

    private int getSpanCount(){
        int orientation = Screen.getOrientation();
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            return 6;
        }else{
           return 4;
        }
    }

}
