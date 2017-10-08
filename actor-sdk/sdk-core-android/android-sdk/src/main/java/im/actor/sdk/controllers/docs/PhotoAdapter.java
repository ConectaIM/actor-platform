package im.actor.sdk.controllers.docs;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.actor.core.entity.Message;
import im.actor.core.entity.content.AbsContent;
import im.actor.core.entity.content.PhotoContent;
import im.actor.core.entity.content.VideoContent;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.R;
import im.actor.sdk.controllers.docs.holders.AbsDocsViewHolder;
import im.actor.sdk.controllers.docs.holders.DefaultViewHolder;
import im.actor.sdk.controllers.docs.holders.PhotoViewHolder;
import im.actor.sdk.util.Screen;

import static im.actor.sdk.controllers.docs.DocsActivity.VIEW_TYPE_PHOTO;
import static im.actor.sdk.controllers.docs.DocsActivity.VIEW_TYPE_VIDEO;

/**
 * Created by diego on 28/08/17.
 */

public class PhotoAdapter extends AbsDocsAdapter {

    interface SpanCountListener {
        void onSpanChange(int spanCount);
    }

    private int viewSize;
    private int spanCount = 4;
    private SpanCountListener spanCountListener;

    public PhotoAdapter(BindedDisplayList<Message> displayList, Activity context,
                        SpanCountListener spanCountListener) {
        super(displayList, context);
        this.spanCountListener = spanCountListener;
    }

    @Override
    public int getItemViewType(int position) {
        AbsContent content = getItem(position).getContent();
        if (PhotoContent.class.isAssignableFrom(content.getClass())) {
            return VIEW_TYPE_PHOTO;
        }
        if (VideoContent.class.isAssignableFrom(content.getClass())) {
            return VIEW_TYPE_VIDEO;
        }
        return -1;
    }

    @Override
    public AbsDocsViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        View itemView = null;
        switch (viewType) {
            case VIEW_TYPE_PHOTO:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_docs_photo, viewGroup, false);
                return new PhotoViewHolder(itemView, this);
            case VIEW_TYPE_VIDEO:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_docs_photo, viewGroup, false);
                return new PhotoViewHolder(itemView, this);
            default:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_docs_default, viewGroup, false);
                return new DefaultViewHolder(itemView, this);
        }
    }

    @Override
    public void onBindViewHolder(AbsDocsViewHolder docsViewHolder, int index, Message item) {
        checkViewSizeAndSpanCount(context.getResources().getConfiguration());

        super.onBindViewHolder(docsViewHolder, index, item);

        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) docsViewHolder.itemView.getLayoutParams();
        params.width = viewSize;
        params.height = viewSize;
        docsViewHolder.itemView.setLayoutParams(params);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        checkViewSizeAndSpanCount(newConfig);
        if (this.spanCountListener != null) {
            this.spanCountListener.onSpanChange(spanCount);
        }
        notifyDataSetChanged();
    }

    private void checkViewSizeAndSpanCount(Configuration configuration) {
        int orientation = configuration.orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 6;
        } else {
            spanCount = 4;
        }
        int screenSize = Screen.getWidth();
        this.viewSize = Math.round(screenSize / spanCount);
    }

    public int getViewSize() {
        return viewSize;
    }

}
