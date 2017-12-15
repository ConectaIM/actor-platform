package im.actor.sdk.controllers.docs.holders;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.view.View;
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

import im.actor.core.entity.FileReference;
import im.actor.core.entity.Message;
import im.actor.core.entity.content.AnimationContent;
import im.actor.core.entity.content.DocumentContent;
import im.actor.core.entity.content.FileLocalSource;
import im.actor.core.entity.content.FileRemoteSource;
import im.actor.core.entity.content.PhotoContent;
import im.actor.core.entity.content.VideoContent;
import im.actor.core.viewmodel.CompressVideoCallback;
import im.actor.core.viewmodel.FileCallback;
import im.actor.core.viewmodel.FileVMCallback;
import im.actor.core.viewmodel.UploadFileCallback;
import im.actor.runtime.files.FileSystemReference;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.Intents;
import im.actor.sdk.controllers.conversation.view.FastBitmapDrawable;
import im.actor.sdk.controllers.conversation.view.FastThumbLoader;
import im.actor.sdk.controllers.docs.AbsDocsAdapter;
import im.actor.sdk.controllers.docs.PhotoAdapter;
import im.actor.sdk.util.Screen;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;
import static im.actor.sdk.util.ViewUtils.goneView;
import static im.actor.sdk.util.ViewUtils.showView;

/**
 * Created by diego on 14/09/17.
 */

public class PhotoViewHolder extends AbsDocsViewHolder {

    //Content Views
    protected SimpleDraweeView previewView;
    private FastThumbLoader fastThumbLoader;

    protected TextView duration;

    //Progress
    protected View progressContainer;
    protected TextView progressValue;
    protected CircularView progressView;
    protected ImageView progressIcon;

    protected boolean isPhoto;
    protected boolean isAnimation;

    private final ControllerListener animationController;
    private Animatable anim;

    private boolean playRequested = false;

    public PhotoViewHolder(View itemView, AbsDocsAdapter adapter) {
        super(itemView, adapter);

        previewView = itemView.findViewById(R.id.image);

        animationController = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(
                    String id,
                    ImageInfo imageInfo,
                    Animatable anim) {
                PhotoViewHolder.this.anim = anim;
                playAnimation();
            }
        };

        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(adapter.getContext().getResources());

        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(200)
                .setRoundingParams(new RoundingParams()
                        .setCornersRadius(Screen.dp(2))
                        .setRoundingMethod(RoundingParams.RoundingMethod.BITMAP_ONLY))
                .build();
        previewView.setHierarchy(hierarchy);

        fastThumbLoader = new FastThumbLoader(previewView);

        duration = itemView.findViewById(R.id.duration);

        progressContainer = itemView.findViewById(R.id.progressBg);
        progressValue = itemView.findViewById(R.id.progressValue);
        progressValue.setTextColor(ActorSDK.sharedActor().style.getTextPrimaryInvColor());
        progressView = itemView.findViewById(R.id.progressView);
        progressView.setColor(Color.WHITE);
        progressIcon = itemView.findViewById(R.id.contentIcon);

    }

    @Override
    public void bindData(Message message, boolean isNewMessage) {

        DocumentContent fileMessage = (DocumentContent) message.getContent();

        if (isNewMessage) {
            if (message.getContent() instanceof PhotoContent) {
                isPhoto = true;
                isAnimation = false;
                duration.setVisibility(View.GONE);
            } else if (message.getContent() instanceof AnimationContent) {
                isPhoto = true;
                isAnimation = true;
                duration.setVisibility(View.VISIBLE);
                duration.setText("");
            } else if (message.getContent() instanceof VideoContent) {
                isPhoto = false;
                isAnimation = false;
                duration.setVisibility(View.VISIBLE);
                duration.setText(messenger().getFormatter().formatDuration(((VideoContent) message.getContent()).getDuration()));
            } else {
                throw new RuntimeException("Unsupported content");
            }

            previewView.setLayoutParams(new ConstraintLayout.LayoutParams(getAdapter().getViewSize(), getAdapter().getViewSize()));
        }

        boolean needRebind = false;

        if (isNewMessage) {
            fastThumbLoader.cancel();

            if (downloadFileVM != null) {
                downloadFileVM.detach();
                downloadFileVM = null;
            }

            needRebind = true;
        }

        if (needRebind) {

            playRequested = false;

            progressContainer.setVisibility(View.GONE);
            progressView.setVisibility(View.GONE);
            progressValue.setVisibility(View.GONE);
            progressIcon.setVisibility(View.GONE);

            if (fileMessage.getSource() instanceof FileRemoteSource) {
                boolean autoDownload = false;
                if (fileMessage instanceof PhotoContent) {
                    autoDownload = messenger().isImageAutoDownloadEnabled();
                } else if (fileMessage instanceof AnimationContent) {
                    autoDownload = messenger().isAnimationAutoPlayEnabled();
                } else if (fileMessage instanceof VideoContent) {
                    autoDownload = messenger().isVideoAutoDownloadEnabled();
                }
                downloadFileVM = messenger().bindFile(((FileRemoteSource) fileMessage.getSource()).getFileReference(),
                        autoDownload, new PhotoViewHolder.DownloadVMCallback(fileMessage));
            }
        }

    }

    private PhotoAdapter getAdapter() {
        return (PhotoAdapter) adapter;
    }

    @Override
    public void onClick(View view) {
        final DocumentContent document = (DocumentContent) currentMessage.getContent();
        if (document.getSource() instanceof FileRemoteSource) {
            FileRemoteSource remoteSource = (FileRemoteSource) document.getSource();
            final FileReference location = remoteSource.getFileReference();
            messenger().requestState(location.getFileId(), new FileCallback() {
                @Override
                public void onNotDownloaded() {
                    messenger().startDownloading(location);
                    playRequested = true;
                }

                @Override
                public void onDownloading(float progress) {
                    messenger().cancelDownloading(location.getFileId());
                }

                @Override
                public void onDownloaded(final FileSystemReference reference) {
                    im.actor.runtime.Runtime.postToMainThread(new Runnable() {
                        @Override
                        public void run() {
                            if (document instanceof PhotoContent) {
                                Intents.openMedia(getAdapter().getContext(), previewView, reference.getDescriptor(), currentMessage.getSenderId());
                            } else if (document instanceof VideoContent) {
                                playVideo(document, reference);
                            } else if (document instanceof AnimationContent) {
                                toggleAnimation();
                            }
                        }
                    });
                }
            });
        } else if (document.getSource() instanceof FileLocalSource) {
            if (document instanceof VideoContent
                    && !((VideoContent) document).isCompressed()) {
                messenger().requestVideoCompressState(currentMessage.getRid(), new CompressVideoCallback() {
                    @Override
                    public void onNotConpressing() {
                        messenger().resumeVideoCompressing(currentMessage.getRid());
                    }

                    @Override
                    public void onCompressing(float progress) {
                        //Nothing to do
                    }

                    @Override
                    public void onCompressed() {
                        //Nothing to do
                    }
                });

            } else {
                messenger().requestUploadState(currentMessage.getRid(), new UploadFileCallback() {
                    @Override
                    public void onNotUploading() {
                        messenger().resumeUpload(currentMessage.getRid());
                    }

                    @Override
                    public void onUploading(float progress) {
                        messenger().pauseUpload(currentMessage.getRid());
                    }

                    @Override
                    public void onUploaded() {
                        // Nothing to do
                    }
                });
            }
        }
    }

    @Override
    public void unbind() {

        // Unbinding model
        if (downloadFileVM != null) {
            downloadFileVM.detach();
            downloadFileVM = null;
        }

        // Releasing images
        fastThumbLoader.cancel();
        previewView.setImageURI(Uri.EMPTY);
        previewView.destroyDrawingCache();

        playRequested = false;

    }

    private void playAnimation() {
        playAnimation(messenger().isAnimationAutoPlayEnabled());
    }

    private void playAnimation(boolean play) {
        if (anim != null) {
            if (play) {
                anim.start();
            } else {
                anim.stop();
            }
        }
    }

    private void toggleAnimation() {
        if (anim != null) {
            if (anim.isRunning()) {
                anim.stop();
            } else {
                anim.start();
            }
        }
    }

    public void playVideo(DocumentContent document, FileSystemReference reference) {
        adapter.getContext().startActivity(Intents.openDoc(adapter.getContext(), document.getName(), reference.getDescriptor()));
    }

    private class DownloadVMCallback implements FileVMCallback {

        private boolean isFastThumbLoaded = false;
        private DocumentContent doc;

        private DownloadVMCallback(DocumentContent doc) {
            this.doc = doc;
        }

        private void checkFastThumb() {
            if (!isFastThumbLoaded) {
                isFastThumbLoaded = true;
                if (doc.getFastThumb() != null) {
                    fastThumbLoader.request(doc.getFastThumb().getImage());
                }
            }
        }

        @Override
        public void onNotDownloaded() {
            checkFastThumb();
            showView(progressContainer);

            progressIcon.setImageResource(R.drawable.conv_media_download);
            showView(progressIcon);

            goneView(progressView);
            goneView(progressValue);
        }

        @Override
        public void onDownloading(float progress) {
            checkFastThumb();
            showView(progressContainer);

            goneView(progressIcon);

            int val = (int) (100 * progress);
            progressValue.setText(val + "");
            progressView.setValue(val);
            showView(progressView);
            showView(progressValue);

        }

        @Override
        public void onDownloaded(FileSystemReference reference) {
            if (isPhoto) {
                previewView.destroyDrawingCache();
                previewView.buildDrawingCache();
                Bitmap drawingCache = previewView.getDrawingCache();
                if (drawingCache != null && !drawingCache.isRecycled()) {
                    previewView.getHierarchy().setPlaceholderImage(new FastBitmapDrawable(drawingCache));
                }
                Uri uri = Uri.fromFile(new File(reference.getDescriptor()));
                bindImage(uri);
                if (isAnimation) {
                    checkFastThumb();
                }
            } else {
                checkFastThumb();
                if (playRequested) {
                    playRequested = false;
                    playVideo((DocumentContent) currentMessage.getContent(), reference);
                }
            }

            progressValue.setText(100 + "");
            progressView.setValue(100);

            goneView(progressContainer);
            goneView(progressView);
            goneView(progressValue);
        }
    }

    public void bindImage(Uri uri) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(previewView.getLayoutParams().width,
                        previewView.getLayoutParams().height))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(previewView.getController())
                .setImageRequest(request)
                .setControllerListener(animationController)
                .build();
        previewView.setController(controller);
    }
}