package im.actor.sdk.controllers.docs.holders;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.droidkit.progress.CircularView;

import im.actor.core.entity.FileReference;
import im.actor.core.entity.Message;
import im.actor.core.entity.content.DocumentContent;
import im.actor.core.entity.content.FileLocalSource;
import im.actor.core.entity.content.FileRemoteSource;
import im.actor.core.entity.content.PhotoContent;
import im.actor.core.viewmodel.FileCallback;
import im.actor.core.viewmodel.FileVM;
import im.actor.core.viewmodel.FileVMCallback;
import im.actor.core.viewmodel.UploadFileCallback;
import im.actor.core.viewmodel.UploadFileVM;
import im.actor.core.viewmodel.UploadFileVMCallback;
import im.actor.runtime.Log;
import im.actor.runtime.files.FileSystemReference;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.Intents;
import im.actor.sdk.controllers.docs.AbsDocsAdapter;
import im.actor.sdk.controllers.docs.DocsAdapter;
import im.actor.sdk.util.FileTypes;
import im.actor.sdk.util.images.common.ImageLoadException;
import im.actor.sdk.util.images.ops.ImageLoading;
import im.actor.sdk.view.TintImageView;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;
import static im.actor.sdk.util.ViewUtils.goneView;
import static im.actor.sdk.util.ViewUtils.showView;

/**
 * Created by diego on 14/09/17.
 */

public class DocsViewHolder extends AbsDocsViewHolder {

    private static final String TAG = DocsViewHolder.class.getName();

    // Content views
    protected TextView fileName;
    protected TextView fileSize;
    protected TextView status;
    protected ImageView fileIcon;

    protected View menu;

    // Progress views
    protected TintImageView downloadIcon;
    protected CircularView progressView;
    protected TextView progressValue;

    // Binded model
    protected FileVM downloadFileVM;
    protected UploadFileVM uploadFileVM;
    protected DocumentContent document;

    public DocsViewHolder(View itemView, AbsDocsAdapter adapter) {
        super(itemView, adapter);

        menu = itemView.findViewById(R.id.menu);
        menu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(itemView.getContext(), v);
            popup.getMenuInflater().inflate(R.menu.doc_popup, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (currentMessage != null && currentMessage.getContent() instanceof DocumentContent) {
                    final DocumentContent documentContent = (DocumentContent) currentMessage.getContent();
                    if (documentContent.getSource() instanceof FileRemoteSource) {
                        FileRemoteSource remoteSource = (FileRemoteSource) documentContent.getSource();
                        messenger().requestState(remoteSource.getFileReference().getFileId(), new FileCallback() {
                            @Override
                            public void onNotDownloaded() {

                            }

                            @Override
                            public void onDownloading(float progress) {

                            }

                            @Override
                            public void onDownloaded(FileSystemReference reference) {
                                Context activity = itemView.getContext();
                                activity.startActivity(Intents.shareDoc(documentContent.getName(),
                                        reference.getDescriptor()));
                            }
                        });
                    }

                }
                return true;
            });
            popup.show();
        });

        // Content views
        fileName = (TextView) itemView.findViewById(R.id.fileName);
        fileName.setTextColor(ActorSDK.sharedActor().style.getTextPrimaryColor());
        fileSize = (TextView) itemView.findViewById(R.id.fileSize);
        fileSize.setTextColor(ActorSDK.sharedActor().style.getTextHintColor());
        status = (TextView) itemView.findViewById(R.id.status);
        status.setTextColor(ActorSDK.sharedActor().style.getTextSecondaryColor());
        fileIcon = (ImageView) itemView.findViewById(R.id.icon);

        // Progress views
        downloadIcon = (TintImageView) itemView.findViewById(R.id.downloading);
        downloadIcon.setTint(ActorSDK.sharedActor().style.getMainColor());
        progressView = (CircularView) itemView.findViewById(R.id.progressView);
        progressView.setColor(ActorSDK.sharedActor().style.getMainColor());
        progressValue = (TextView) itemView.findViewById(R.id.progressValue);
        progressValue.setTextColor(ActorSDK.sharedActor().style.getTextSecondaryColor());

    }

    @Override
    public void bindData(Message message, boolean isUpdated) {

        document = (DocumentContent) message.getContent();

        fileName.setText(document.getName());
        fileSize.setText(messenger().getFormatter().formatFileSize(document.getSource().getSize())
                + " " + document.getExt().toUpperCase());

        boolean isAppliedThumb = false;
        if (document.getFastThumb() != null) {
            try {
                Bitmap img = ImageLoading.loadBitmap(document.getFastThumb().getImage());
                fileIcon.setImageBitmap(img);
                fileIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
                isAppliedThumb = true;
            } catch (ImageLoadException e) {
                Log.e(TAG, e);
            }
        }

        if (isUpdated) {
            if (!isAppliedThumb) {
                int type = FileTypes.getType(document.getExt());
                switch (type) {
                    default:
                    case FileTypes.TYPE_UNKNOWN:
                        fileIcon.setImageResource(R.drawable.picker_unknown);
                        break;
                    case FileTypes.TYPE_APK:
                        fileIcon.setImageResource(R.drawable.picker_apk);
                        break;
                    case FileTypes.TYPE_MUSIC:
                        fileIcon.setImageResource(R.drawable.picker_music);
                        break;
                    case FileTypes.TYPE_PICTURE:
                        fileIcon.setImageResource(R.drawable.picker_unknown);
                        break;
                    case FileTypes.TYPE_DOC:
                        fileIcon.setImageResource(R.drawable.picker_doc);
                        break;
                    case FileTypes.TYPE_RAR:
                        fileIcon.setImageResource(R.drawable.picker_rar);
                        break;
                    case FileTypes.TYPE_VIDEO:
                        fileIcon.setImageResource(R.drawable.picker_video);
                        break;
                    case FileTypes.TYPE_ZIP:
                        fileIcon.setImageResource(R.drawable.picker_zip);
                        break;
                    case FileTypes.TYPE_XLS:
                        fileIcon.setImageResource(R.drawable.picker_xls);
                        break;
                    case FileTypes.TYPE_PPT:
                        fileIcon.setImageResource(R.drawable.picker_ppt);
                        break;
                    case FileTypes.TYPE_CSV:
                        fileIcon.setImageResource(R.drawable.picker_csv);
                        break;
                    case FileTypes.TYPE_HTM:
                        fileIcon.setImageResource(R.drawable.picker_htm);
                        break;
                    case FileTypes.TYPE_HTML:
                        fileIcon.setImageResource(R.drawable.picker_html);
                        break;
                    case FileTypes.TYPE_PDF:
                        fileIcon.setImageResource(R.drawable.picker_pdf);
                        break;
                }
                fileIcon.setScaleType(ImageView.ScaleType.CENTER);
            }
        }

        boolean needRebind = false;
        if (isUpdated) {
            // Resetting binding
            if (downloadFileVM != null) {
                downloadFileVM.detach();
                downloadFileVM = null;
            }
            if (uploadFileVM != null) {
                uploadFileVM.detach();
                uploadFileVM = null;
            }
            needRebind = true;
        } else {
            if (document.getSource() instanceof FileLocalSource) {
                if (uploadFileVM == null && downloadFileVM != null) {
                    downloadFileVM.detach();
                    downloadFileVM = null;
                    needRebind = true;
                }
            } else if (document.getSource() instanceof FileRemoteSource) {
                if (uploadFileVM != null && downloadFileVM == null) {
                    uploadFileVM.detach();
                    uploadFileVM = null;
                    needRebind = true;
                }
            }
        }

        if (downloadFileVM == null && uploadFileVM == null) {
            needRebind = true;
        }

        if (needRebind) {
            downloadIcon.setVisibility(View.GONE);
            progressView.setVisibility(View.GONE);
            progressValue.setVisibility(View.GONE);
            fileIcon.setVisibility(View.GONE);
            status.setVisibility(View.GONE);

            if (document.getSource() instanceof FileRemoteSource) {
                FileRemoteSource remoteSource = (FileRemoteSource) document.getSource();
                boolean autoDownload = remoteSource.getFileReference().getFileSize() <= 1024 * 1024
                        && messenger().isDocAutoDownloadEnabled();// < 1MB
                downloadFileVM = messenger().bindFile(remoteSource.getFileReference(),
                        autoDownload, new DocsViewHolder.DownloadVMCallback());
            } else if (document.getSource() instanceof FileLocalSource) {
                uploadFileVM = messenger().bindUpload(message.getRid(),
                        new DocsViewHolder.UploadVMCallback());
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
        if (uploadFileVM != null) {
            uploadFileVM.detach();
            uploadFileVM = null;
        }
    }

    private DocsAdapter getAdapter() {
        return (DocsAdapter) adapter;
    }

    @Override
    public void onClick(View view) {

        if (document.getSource() instanceof FileRemoteSource) {
            FileRemoteSource remoteSource = (FileRemoteSource) document.getSource();
            final FileReference location = remoteSource.getFileReference();
            messenger().requestState(location.getFileId(), new FileCallback() {
                @Override
                public void onNotDownloaded() {
                    messenger().startDownloading(location);
                }

                @Override
                public void onDownloading(float progress) {
                    messenger().cancelDownloading(location.getFileId());
                }

                @Override
                public void onDownloaded(final FileSystemReference reference) {
                    im.actor.runtime.Runtime.postToMainThread(() -> {
                        if (document instanceof PhotoContent) {
                            Intents.openMedia(getAdapter().getContext(), fileIcon, reference.getDescriptor(), currentMessage.getSenderId());
                        } else {
                            try {
                                Activity activity = getAdapter().getContext();
                                activity.startActivity(Intents.openDoc(activity, document.getName(), reference.getDescriptor()));
                            } catch (Exception e) {
                                Log.e(TAG, e);
                                Toast.makeText(getAdapter().getContext(), R.string.toast_unable_open, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
        } else if (document.getSource() instanceof FileLocalSource) {
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

    private class DownloadVMCallback implements FileVMCallback {

        @Override
        public void onNotDownloaded() {
            status.setText(R.string.chat_doc_download);
            showView(status);

            // File Icon
            goneView(fileIcon);

            downloadIcon.setResource(R.drawable.ic_cloud_download_white_36dp);
            showView(downloadIcon);
            progressView.setValue(0);
            goneView(progressValue);
            goneView(progressView);
        }

        @Override
        public void onDownloading(float progress) {
            status.setText(R.string.chat_doc_stop);
            showView(status);

            goneView(fileIcon);

            goneView(downloadIcon);
            int val = (int) (progress * 100);
            progressView.setValue(val);
            progressValue.setText("" + val);
            showView(progressView);
            showView(progressValue);
        }

        @Override
        public void onDownloaded(FileSystemReference reference) {
            status.setText(R.string.chat_doc_open);
            showView(status);

            showView(fileIcon);

            goneView(downloadIcon);
            goneView(progressValue);
            goneView(progressView);
        }
    }

    private class UploadVMCallback implements UploadFileVMCallback {

        @Override
        public void onNotUploaded() {
            status.setText(R.string.chat_doc_send);
            showView(status);

            // File Icon
            goneView(fileIcon);

            downloadIcon.setResource(R.drawable.ic_cloud_upload_white_36dp);
            showView(downloadIcon);
            progressView.setValue(0);
            goneView(progressValue);
            goneView(progressView);
        }

        @Override
        public void onUploading(float progress) {
            status.setText(R.string.chat_doc_stop);
            showView(status);

            goneView(fileIcon);

            goneView(downloadIcon);
            int val = (int) (progress * 100);
            progressView.setValue(val);
            progressValue.setText("" + val);
            showView(progressView);
            showView(progressValue);
        }

        @Override
        public void onUploaded() {
            onUploading(1);
        }
    }
}
