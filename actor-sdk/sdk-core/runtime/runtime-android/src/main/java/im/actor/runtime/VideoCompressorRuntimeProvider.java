package im.actor.runtime;

import java.io.File;

import br.com.diegosilva.vo.vclibrary.video.ConversionListener;
import br.com.diegosilva.vo.vclibrary.video.MediaController;
import im.actor.core.entity.CompressedVideo;
import im.actor.core.modules.file.CompressVideoManager;
import im.actor.runtime.files.FileSystemReference;
import im.actor.runtime.promise.Promise;
import im.actor.runtime.promise.PromiseFunc;

/**
 * Created by diego on 13/05/17.
 */

public class VideoCompressorRuntimeProvider {

    public Promise<CompressedVideo> compressVideo(CompressVideoManager.CompressItem item, CompressorProgressListener progressCallback){
        return new Promise<>((PromiseFunc<CompressedVideo>) resolver -> {
            new Thread(()->{
                FileSystemReference fr = Storage.createTempFile();
                String destPath = fr.getDescriptor();
                MediaController.getInstance().convertVideo(item.getOriginalFilePath(), destPath, new ConversionListener() {
                    @Override
                    public void onError(Exception e) {
                        resolver.error(new RuntimeException(("Error when compressing video")));
                    }

                    @Override
                    public void onProgress(float v) {
                        if(progressCallback != null){
                            progressCallback.onProgress(item.getRid(), v);
                        }
                    }

                    @Override
                    public void onSuccess(File file) {
                        new File(item.getOriginalFilePath()).delete();
                        file.renameTo(new File(item.getOriginalFilePath()));
                        resolver.result(new CompressedVideo(item.getRid(), file.getName(), file.getAbsolutePath(), item.getSender()));
                    }
                });

            }).start();
        });
    }


    public interface CompressorProgressListener {
        void onProgress(long rid, float v);
    }

}
