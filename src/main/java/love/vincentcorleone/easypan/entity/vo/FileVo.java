package love.vincentcorleone.easypan.entity.vo;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileVo {

    public String fileName;
    public String lastModified;

    public String size;

    public boolean isDirectory;

    public FileVo(File file) {
        this.fileName = file.getName();
        Date date = new Date(file.lastModified());
        String pattern1 = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf1 = new SimpleDateFormat(pattern1);
        this.lastModified = sdf1.format(date);
        this.size = this.parseSize(file.length());
        this.isDirectory = file.isDirectory();
    }

    private String parseSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }
}
