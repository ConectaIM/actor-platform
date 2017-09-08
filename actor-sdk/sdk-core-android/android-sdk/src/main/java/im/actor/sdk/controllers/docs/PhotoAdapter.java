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
import im.actor.core.entity.content.PhotoContent;
import im.actor.core.entity.content.VideoContent;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.R;
import im.actor.sdk.util.Screen;

import static im.actor.sdk.controllers.docs.DocsActivity.VIEW_TYPE_PHOTO;
import static im.actor.sdk.controllers.docs.DocsActivity.VIEW_TYPE_VIDEO;

/**
 * Created by diego on 28/08/17.
 */

public class PhotoAdapter extends DocsAdapter {

    interface SpanCountListener{
        void onSpanChange(int spanCount);
    }

    private int viewSize;
    private int spanCount = 4;
    private SpanCountListener spanCountListener;


    public PhotoAdapter(BindedDisplayList<Message> displayList, Context context, SpanCountListener spanCountListener) {
        super(displayList, context);
        this.spanCountListener = spanCountListener;
    }

    @Override
    public int getItemViewType(int position) {
        AbsContent content = getItem(position).getContent();
        if(content.getClass().isAssignableFrom(PhotoContent.class)){
            return VIEW_TYPE_PHOTO;
        }else if(content.getClass().isAssignableFrom(VideoContent.class)){
            return VIEW_TYPE_VIDEO;
        }
        return -1;
    }

    @Override
    public DocsViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        View itemView = null;
        switch (viewType){
            case VIEW_TYPE_PHOTO :
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_docs_photo, viewGroup, false);
                return new PhotoViewHolder(itemView);
            case VIEW_TYPE_VIDEO:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_docs_video, viewGroup, false);
                return new DocumentViewHolder(itemView);
            default:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_docs_default, viewGroup, false);
                return new DefaultViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(DocsViewHolder docsViewHolder, int index, Message item) {
        super.onBindViewHolder(docsViewHolder, index, item);

        checkViewSizeAndSpanCount(context.getResources().getConfiguration());

        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) docsViewHolder.itemView.getLayoutParams();
        params.width = viewSize;
        params.height = viewSize;
        docsViewHolder.itemView.setLayoutParams(params);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        checkViewSizeAndSpanCount(newConfig);
        if(this.spanCountListener != null){
            this.spanCountListener.onSpanChange(spanCount);
        }
        notifyDataSetChanged();
    }

    private void checkViewSizeAndSpanCount(Configuration configuration){
        int orientation = configuration.orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            spanCount = 6;
        }else{
            spanCount = 4;
        }
        int screenSize = Screen.getWidth();
        this.viewSize = Math.round(screenSize/spanCount);
    }


    class PhotoViewHolder extends DocsViewHolder{

        public PhotoViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindData(Message message, Message prev, Message next) {

        }

        @Override
        public void unbind() {

        }
    }


    class VideoViewHolder extends DocsViewHolder{
        public VideoViewHolder(View itemView) {
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
