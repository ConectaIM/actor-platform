package im.actor.sdk.controllers.docs;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import im.actor.core.entity.Message;
import im.actor.core.entity.Peer;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.R;
import im.actor.sdk.controllers.DisplayListFragment;
import im.actor.sdk.controllers.docs.holders.AbsDocsViewHolder;

/**
 * Created by dsilv on 06/08/2017.
 */

public abstract class AbsDocsFragment extends DisplayListFragment<Message, AbsDocsViewHolder> {


    protected Peer peer;
    private CircularProgressBar progressView;

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        peer = Peer.fromUniqueId(getArguments().getLong("peer"));
    }

    protected abstract BindedDisplayList<Message> onCreateDisplayList();

    //
    // View
    //
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        BindedDisplayList<Message> displayList = onCreateDisplayList();

        View res = inflate(inflater, container, R.layout.fragment_shared_media, displayList);

        progressView = (CircularProgressBar) res.findViewById(R.id.loadingProgress);
        progressView.setIndeterminate(true);
        progressView.setVisibility(View.INVISIBLE);

        return res;
    }

    @Override
    protected void configureRecyclerView(RecyclerView recyclerView) {
        super.configureRecyclerView(recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setHorizontalScrollBarEnabled(false);
        recyclerView.setVerticalScrollBarEnabled(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ((AbsDocsAdapter) getAdapter()).onConfigurationChanged(newConfig);
    }

}
