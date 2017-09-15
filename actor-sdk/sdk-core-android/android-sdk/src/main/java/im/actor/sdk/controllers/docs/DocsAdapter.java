package im.actor.sdk.controllers.docs;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.actor.core.entity.Message;
import im.actor.core.entity.content.AbsContent;
import im.actor.core.entity.content.DocumentContent;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.R;
import im.actor.sdk.controllers.docs.holders.DefaultViewHolder;
import im.actor.sdk.controllers.docs.holders.AbsDocsViewHolder;
import im.actor.sdk.controllers.docs.holders.DocsViewHolder;
import im.actor.sdk.controllers.docs.holders.PhotoViewHolder;
import im.actor.sdk.util.Screen;

import static im.actor.sdk.controllers.docs.DocsActivity.VIEW_TYPE_DOCUMENT;
import static im.actor.sdk.controllers.docs.DocsActivity.VIEW_TYPE_VIDEO;

/**
 * Created by diego on 28/08/17.
 */

public class DocsAdapter extends AbsDocsAdapter {


    public DocsAdapter(BindedDisplayList<Message> displayList, Context context) {
        super(displayList, context);
    }

    @Override
    public int getItemViewType(int position) {
        AbsContent content = getItem(position).getContent();
        if (content.getClass().isAssignableFrom(DocumentContent.class)) {
            return VIEW_TYPE_DOCUMENT;
        }
        return -1;
    }

    @Override
    public AbsDocsViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        View itemView = null;
        switch (viewType) {
            case VIEW_TYPE_DOCUMENT:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_docs_document, viewGroup, false);
                return new DocsViewHolder(itemView, this);
            default:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_docs_default, viewGroup, false);
                return new DefaultViewHolder(itemView, this);
        }
    }

    @Override
    public void onBindViewHolder(AbsDocsViewHolder docsViewHolder, int index, Message item) {
        super.onBindViewHolder(docsViewHolder, index, item);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //do nothing
    }


}
