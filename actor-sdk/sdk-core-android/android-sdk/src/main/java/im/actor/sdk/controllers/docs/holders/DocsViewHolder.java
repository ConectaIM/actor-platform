package im.actor.sdk.controllers.docs.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import im.actor.core.entity.Message;
import im.actor.core.viewmodel.FileVM;
import im.actor.sdk.controllers.docs.DocsAdapter;

/**
 * Created by diego on 14/09/17.
 */

public abstract class DocsViewHolder extends RecyclerView.ViewHolder{

    protected FileVM downloadFileVM;
    protected Message currentMessage;
    protected DocsAdapter adapter;

    public DocsViewHolder(View itemView, DocsAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
    }

    public void bindData(Message message){
        this.currentMessage = message;
    }

    public abstract void unbind();
}