package im.actor.sdk.controllers.docs;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import im.actor.core.entity.Message;
import im.actor.core.entity.Peer;
import im.actor.runtime.android.view.BindedListAdapter;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.controllers.docs.holders.AbsDocsViewHolder;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

/**
 * Created by diego on 28/08/17.
 */

public class DocsFragment extends AbsDocsFragment {

    public static DocsFragment create(Peer peer) {
        DocsFragment res = new DocsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("peer", peer.getUnuqueId());
        res.setArguments(bundle);
        return res;
    }

    @Override
    protected BindedListAdapter<Message, AbsDocsViewHolder> onCreateAdapter(BindedDisplayList<Message> displayList, Activity activity) {
        return new DocsAdapter(displayList, activity);
    }

    @Override
    protected BindedDisplayList<Message> onCreateDisplayList() {
        BindedDisplayList<Message> displayList = messenger().getDocsDisplayList(peer);
        return displayList;
    }

    @Override
    protected void configureRecyclerView(RecyclerView recyclerView) {
        super.configureRecyclerView(recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setRecycleChildrenOnDetach(false);
        linearLayoutManager.setSmoothScrollbarEnabled(false);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

}