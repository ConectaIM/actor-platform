package im.actor.sdk.controllers.media.view;

import android.content.Context;
import android.view.ViewGroup;

import im.actor.core.entity.Dialog;
import im.actor.core.entity.Message;
import im.actor.runtime.android.view.BindedListAdapter;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.controllers.dialogs.view.DialogHolder;
import im.actor.sdk.view.adapters.OnItemClickedListener;

/**
 * Created by diego on 11/08/17.
 */

public class SharedMediaAdapter extends BindedListAdapter<Message, SharedMediaHolder> {

    private OnItemClickedListener<Message> onItemClicked;
    private Context context;

    public SharedMediaAdapter(BindedDisplayList<Message> displayList, OnItemClickedListener<Message> onItemClicked,
                          Context context) {
        super(displayList);
        this.context = context;
        this.onItemClicked = onItemClicked;
    }

    @Override
    public SharedMediaHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new SharedMediaHolder(new DialogView(context), onItemClicked);
    }

    @Override
    public void onBindViewHolder(SharedMediaHolder dialogHolder, int index, Message item) {

    }
}
