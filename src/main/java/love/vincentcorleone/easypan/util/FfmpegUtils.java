package love.vincentcorleone.easypan.util;

import love.vincentcorleone.easypan.Constants;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Component
public class FfmpegUtils {

    public static void exec(String command){
        try {
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();

            reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();


            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void splitVideoAndGenScale(String fileName, String currentDir, String nickname){
        String attachDir = FileUtils.initUserAttachmentDir(nickname) + currentDir + fileName;
        File attachDirFile = new File(attachDir);
        if(!attachDirFile.exists()){
            attachDirFile.mkdirs();
        }
        final String Cmd_ToTs = "ffmpeg -y -i %s -vcodec copy -acodec copy -vbsf h264_mp4toannexb %s";
        final String Cmd_CutTs = "ffmpeg -i %s -c copy -map 0 -f segment -segment_list %s -segment_time 30 %s/%s_%%4d.ts";
        String tsPath = attachDir + "/tmp.ts";

        String inFile = FileUtils.initUserRootDir(nickname) + currentDir + fileName;

        String cmd = String.format(Cmd_ToTs,inFile/*源视频文件*/, tsPath/*临时输出文件*/);
        exec(cmd);

        //attachDir是附属文件夹路径
        cmd = String.format(Cmd_CutTs,tsPath,attachDir + "/" + Constants.M3U8_NAME,attachDir,"snippet");
        exec(cmd);

        new File(tsPath).delete();

        final String Cmd_GenCover = "ffmpeg -i %s -y -vframes 1 -vf scale=%d:%d/a %s";
        cmd = String.format(Cmd_GenCover, inFile/*源视频文件*/, Constants.COVER_WIDTH, Constants.COVER_WIDTH, attachDir + "/cover.png" );
        exec(cmd);
    }



    /**
     *
     * @param fileName 上传的文件名
     * @param currentDir 相对路径
     * @param nickname 用户昵称
     */
    @Async
    public void afterUpload(String fileName, String currentDir, String nickname){
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);

        if (Constants.videos.contains(suffix)){
            //切割视频并制作缩略图
            FfmpegUtils.splitVideoAndGenScale(fileName,currentDir,nickname);
        } else if(Constants.images.contains(suffix)){
            FfmpegUtils.genScaleForImage(fileName,currentDir,nickname);
        }

    }

    private static void genScaleForImage(String fileName, String currentDir, String nickname) {
        String attachDir = FileUtils.initUserAttachmentDir(nickname) + currentDir + fileName;
        File attachDirFile = new File(attachDir);
        if(!attachDirFile.exists()){
            attachDirFile.mkdirs();
        }

        String inFile = FileUtils.initUserRootDir(nickname) + currentDir + fileName;

        try {
            BufferedImage src = ImageIO.read(new File(inFile));
            int srcW = src.getWidth();
            if (srcW > Constants.COVER_WIDTH){
                final String Cmd_GenScale = "ffmpeg -i %s -vf scale=%d:-1 %s -y";
                String cmd = String.format(Cmd_GenScale,inFile, Constants.COVER_WIDTH, attachDir + "/cover.png");
                exec(cmd);
            }else{
                Files.copy(Paths.get(inFile),Paths.get(attachDir + "/cover"), StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
