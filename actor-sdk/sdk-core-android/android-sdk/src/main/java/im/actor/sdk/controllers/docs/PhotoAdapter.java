package im.actor.sdk.controllers.docs;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.droidkit.progress.CircularView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;

import im.actor.core.entity.Message;
import im.actor.core.entity.content.AbsContent;
import im.actor.core.entity.content.AnimationContent;
import im.actor.core.entity.content.DocumentContent;
import im.actor.core.entity.content.FileLocalSource;
import im.actor.core.entity.content.FileRemoteSource;
import im.actor.core.entity.content.PhotoContent;
import im.actor.core.entity.content.VideoContent;
import im.actor.core.viewmodel.FileVM;
import im.actor.core.viewmodel.FileVMCallback;
import im.actor.runtime.Log;
import im.actor.runtime.files.FileSystemReference;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.Intents;
import im.actor.sdk.controllers.conversation.messages.content.PhotoHolder;
import im.actor.sdk.controllers.conversation.view.FastBitmapDrawable;
import im.actor.sdk.controllers.conversation.view.FastThumbLoader;
import im.actor.sdk.controllers.docs.holders.DefaultViewHolder;
import im.actor.sdk.controllers.docs.holders.DocsViewHolder;
import im.actor.sdk.controllers.docs.holders.DocumentViewHolder;
import im.actor.sdk.controllers.docs.holders.PhotoViewHolder;
import im.actor.sdk.util.Screen;

import static im.actor.sdk.controllers.docs.DocsActivity.VIEW_TYPE_PHOTO;
import static im.actor.sdk.controllers.docs.DocsActivity.VIEW_TYPE_VIDEO;
import static im.actor.sdk.util.ActorSDKMessenger.messenger;
import static im.actor.sdk.util.ViewUtils.goneView;
import static im.actor.sdk.util.ViewUtils.showView;

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
                return new PhotoViewHolder(itemView, this);
            case VIEW_TYPE_VIDEO:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_docs_video, viewGroup, false);
                return new DocumentViewHolder(itemView, this);
            default:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_docs_default, viewGroup, false);
                return new DefaultViewHolder(itemView, this);
        }
    }

    @Override
    public void onBindViewHolder(DocsViewHolder docsViewHolder, int index, Message item) {
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

    public int getViewSize(){
        return viewSize;
    }

}
