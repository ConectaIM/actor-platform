package im.actor.runtime;

import java.io.File;

import br.com.diegosilva.vo.vclibrary.video.ConversionListener;
import br.com.diegosilva.vo.vclibrary.video.MediaController;
import im.actor.core.entity.CompressedVideo;
import im.actor.core.entity.FileReference;
import im.actor.runtime.files.FileSystemReference;
import im.actor.runtime.promise.Promise;
import im.actor.runtime.promise.PromiseFunc;

/**
 * Created by diego on 13/05/17.
 */

public class VideoCompressorRuntimeProvider {

    public Promise<CompressedVideo> compressVideo(String path){
        return new Promise<>((PromiseFunc<CompressedVideo>) resolver -> {
            new Thread(()->{
                FileSystemReference fr = Storage.createTempFile();
                String destPath = fr.getDescriptor();
                MediaController.getInstance().convertVideo(path, destPath, new ConversionListener() {
                    @Override
                    public void onError(Exception e) {
                        resolver.error(new RuntimeException(("Error when compressing video")));
                    }

                    @Override
                    public void onProgress(float v) {

                    }

                    @Override
                    public void onSuccess(File file) {
                        new File(path).delete();
                        file.renameTo(new File(path));
                        resolver.result(new CompressedVideo(file.getName(), file.getAbsolutePath()));
                    }
                });

            }).start();
        });
    }

}
