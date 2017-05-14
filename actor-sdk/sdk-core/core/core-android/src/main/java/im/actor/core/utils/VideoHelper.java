package im.actor.core.utils;

import java.io.File;

import br.com.diegosilva.vo.vclibrary.video.MediaController;

/**
 * Created by diego on 13/05/17.
 */

public class VideoHelper {

    public static File compressVideo(String path, String videosPath, boolean deleteOriginal){
        return MediaController.getInstance().convertVideo(path, videosPath, deleteOriginal);
    }
}
