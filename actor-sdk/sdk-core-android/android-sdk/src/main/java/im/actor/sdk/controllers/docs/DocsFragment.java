package im.actor.sdk.controllers.docs;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import im.actor.core.entity.Message;
import im.actor.core.entity.Peer;
import im.actor.runtime.android.view.BindedListAdapter;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.R;
import im.actor.sdk.controllers.DisplayListFragment;
import im.actor.sdk.controllers.conversation.ChatFragment;
import im.actor.sdk.controllers.conversation.messages.MessagesAdapter;
import im.actor.sdk.controllers.conversation.messages.content.AbsMessageViewHolder;
import im.actor.sdk.util.Screen;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

/**
 * Created by dsilv on 06/08/2017.
 */

public class DocsFragment extends DisplayListFragment<Message, DocsAdapter.DocsViewHolder> {

    protected Peer peer;
    private CircularProgressBar progressView;
    private DocsAdapter docsAdapter;


    public static DocsFragment create(Peer peer) {
        DocsFragment res = new DocsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("peer", peer.getUnuqueId());
        res.setArguments(bundle);
        return res;
    }

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        peer = Peer.fromUniqueId(getArguments().getLong("peer"));
    }

    @Override
    protected BindedListAdapter<Message, DocsAdapter.DocsViewHolder> onCreateAdapter(BindedDisplayList<Message> displayList, Activity activity) {
        docsAdapter = new DocsAdapter(displayList, activity);
        return docsAdapter;
    }

    protected BindedDisplayList<Message> onCreateDisplayList() {
        BindedDisplayList<Message> displayList = messenger().getDocsDisplayList(peer);
        return displayList;
    }

    //
    // View
    //
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //
        // Display List
        //
        BindedDisplayList<Message> displayList = onCreateDisplayList();


        //
        // Main View
        //
        View res = inflate(inflater, container, R.layout.fragment_shared_media, displayList);

        progressView = (CircularProgressBar) res.findViewById(R.id.loadingProgress);
        progressView.setIndeterminate(true);
        progressView.setVisibility(View.INVISIBLE);


        //
        // List Padding
        //
        View footer = new View(getActivity());
        footer.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Screen.dp(8)));
        addHeaderView(footer); // Add Footer as Header because of reverse layout

        View header = new View(getActivity());
        header.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Screen.dp(64)));
        addFooterView(header); // Add Header as Footer because of reverse layout


        return res;
    }
}
