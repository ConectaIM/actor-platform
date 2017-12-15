package im.actor.sdk.controllers.docs;

import android.app.Activity;
import android.content.res.Configuration;

import im.actor.core.entity.Message;
import im.actor.core.entity.Peer;
import im.actor.runtime.android.view.BindedListAdapter;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.controllers.ActorBinder;
import im.actor.sdk.controllers.docs.holders.AbsDocsViewHolder;


/**
 * Created by diego on 23/08/17.
 */

public abstract class AbsDocsAdapter extends BindedListAdapter<Message, AbsDocsViewHolder> {


    protected ActorBinder BINDER = new ActorBinder();
    protected Activity context;
    protected Peer peer;

    public AbsDocsAdapter(BindedDisplayList<Message> displayList, Activity context) {
        super(displayList);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(AbsDocsViewHolder docsViewHolder, int index, Message item) {

        Message prev = null;
        Message next = null;

        if (index > 1) {
            next = getItem(index - 1);
        }

        if (index < getItemCount() - 1) {
            prev = getItem(index + 1);
        }

        docsViewHolder.bindData(item);
    }


    @Override
    public void onViewRecycled(AbsDocsViewHolder holder) {
        holder.unbind();
    }

    public abstract void onConfigurationChanged(Configuration newConfig);


    public ActorBinder getBinder() {
        return BINDER;
    }

    public Activity getContext() {
        return context;
    }

}
