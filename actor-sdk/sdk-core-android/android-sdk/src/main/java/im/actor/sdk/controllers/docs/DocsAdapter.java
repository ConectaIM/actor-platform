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
import im.actor.runtime.android.view.BindedListAdapter;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.R;
import im.actor.sdk.controllers.ActorBinder;
import im.actor.sdk.util.Screen;


/**
 * Created by diego on 23/08/17.
 */

public abstract class DocsAdapter extends BindedListAdapter<Message, DocsAdapter.DocsViewHolder>{


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

        docsViewHolder.bindData(item, prev, next);
    }


    @Override
    public void onViewRecycled(DocsViewHolder holder) {
        holder.unbind();
    }

    public abstract void onConfigurationChanged(Configuration newConfig);


    public ActorBinder getBinder() {
        return BINDER;
    }


    //Holders
    abstract class DocsViewHolder extends RecyclerView.ViewHolder{
        public DocsViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindData(Message message, Message prev, Message next);

        public abstract void unbind();
    }

    class DefaultViewHolder extends DocsViewHolder{
        public DefaultViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindData(Message message, Message prev, Message next) {

        }

        @Override
        public void unbind() {

        }
    }

    class DocumentViewHolder extends DocsViewHolder{
        public DocumentViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindData(Message message, Message prev, Message next) {

        }

        @Override
        public void unbind() {

        }
    }


}
