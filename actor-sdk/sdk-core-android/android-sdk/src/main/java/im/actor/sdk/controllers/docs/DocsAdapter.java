package im.actor.sdk.controllers.docs;

import android.content.Context;
import android.content.res.Configuration;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.actor.core.entity.Message;
import im.actor.core.entity.Peer;
import im.actor.core.entity.content.AbsContent;
import im.actor.core.entity.content.DocumentContent;
import im.actor.core.entity.content.PhotoContent;
import im.actor.core.entity.content.VideoContent;
import im.actor.core.viewmodel.FileVM;
import im.actor.runtime.android.view.BindedListAdapter;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.controllers.ActorBinder;
import im.actor.sdk.controllers.docs.holders.DocsViewHolder;


/**
 * Created by diego on 23/08/17.
 */

public abstract class DocsAdapter extends BindedListAdapter<Message, DocsViewHolder>{


    protected ActorBinder BINDER = new ActorBinder();
    protected Context context;
    protected Peer peer;

    public DocsAdapter(BindedDisplayList<Message> displayList, Context context) {
        super(displayList);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(DocsViewHolder docsViewHolder, int index, Message item) {

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
    public void onViewRecycled(DocsViewHolder holder) {
        holder.unbind();
    }

    public abstract void onConfigurationChanged(Configuration newConfig);


    public ActorBinder getBinder() {
        return BINDER;
    }

    public Context getContext() {
        return context;
    }
    
}
