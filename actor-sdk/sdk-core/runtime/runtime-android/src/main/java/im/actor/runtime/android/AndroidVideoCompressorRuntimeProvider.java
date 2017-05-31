package im.actor.runtime.android;

import java.io.File;

import br.com.diegosilva.vo.vclibrary.video.ConversionListener;
import br.com.diegosilva.vo.vclibrary.video.MediaController;
import im.actor.runtime.CompressorProgressListener;
import im.actor.runtime.Storage;
import im.actor.runtime.VideoCompressorRuntime;
import im.actor.runtime.actors.ActorRef;
import im.actor.runtime.files.FileSystemReference;
import im.actor.runtime.promise.Promise;
import im.actor.runtime.promise.PromiseFunc;
import im.actor.runtime.video.CompressedVideo;

/**
 * Created by diego on 23/05/17.
 */

public class AndroidVideoCompressorRuntimeProvider implements VideoCompressorRuntime {

    @Override
    public Promise<CompressedVideo> compressVideo(long rid, String originalPath, ActorRef sender, CompressorProgressListener progressCallback) {
        return new Promise<>((PromiseFunc<CompressedVideo>) resolver -> {
            new Thread(() -> {
                try {
                    FileSystemReference fr = Storage.createTempFile();
                    String destPath = fr.getDescriptor();
                    MediaController.getInstance().convertVideo(originalPath, destPath, new ConversionListener() {
                        @Override
                        public void onError(Exception e) {
                            resolver.error(new RuntimeException(("Error when compressing video")));
                        }

                        @Override
                        public void onProgress(float v) {
                            if (progressCallback != null) {
                                progressCallback.onProgress(rid, v);
                            }
                        }

                        @Override
                        public void onSuccess(File tempFile) {
                            File originalFile = new File(originalPath);
                            originalFile.delete();
                            tempFile.renameTo(originalFile);
                            resolver.result(new CompressedVideo(rid, originalFile.getName(), originalFile.getAbsolutePath(), sender));
                        }
                    });
                } catch (Exception e) {
                    resolver.error(e);
                }
            }).start();
        });
    }
}
