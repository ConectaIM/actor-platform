package im.actor.runtime;

import java.io.File;

import br.com.diegosilva.vo.vclibrary.video.MediaController;
import im.actor.core.entity.CompressedVideo;

/**
 * Created by diego on 13/05/17.
 */

public class VideoCompressorRuntimeProvider {

    public CompressedVideo compressVideo(String path, String destinyPath, boolean deleteOriginal){
        File f = MediaController.getInstance().convertVideo(path, destinyPath, deleteOriginal);

        if(f != null){
            return new CompressedVideo(f.getName(), f.getAbsolutePath());
        }

        return null;
    }

}
