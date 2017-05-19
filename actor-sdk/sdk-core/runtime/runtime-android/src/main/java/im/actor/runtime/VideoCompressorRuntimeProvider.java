package im.actor.runtime;

import java.io.File;

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

    public Promise<CompressedVideo> compressVideo(String path, String destinyPath, boolean deleteOriginal){
        return new Promise<>((PromiseFunc<CompressedVideo>) resolver -> {
            new Thread(()->{
                FileSystemReference fr = Storage.createTempFile();
                String destPath = fr.getDescriptor();
                File f = MediaController.getInstance().convertVideo(path, destPath, deleteOriginal);
                if(f != null){
                    if(f.exists()){
                        new File(path).delete();
                        f.renameTo(new File(path));
                    }
                    resolver.result(new CompressedVideo(f.getName(), f.getAbsolutePath()));
                }else{
                    resolver.error(new RuntimeException(("Error when compressing video")));
                }
            }).start();
        });
    }

}
