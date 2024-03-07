package love.vincentcorleone.easypan.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("large_file")
public class LargeFile {
    private Long id;

    private Long userId;

    private String viewDir;

    private String fileName;

    private boolean isPublic;

    private String md5;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getViewDir() {
        return viewDir;
    }

    public void setViewDir(String viewDir) {
        this.viewDir = viewDir;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getDiskPath(String ownerBasePath){
        return ownerBasePath + this.getViewDir() + this.getFileName();
    }
}
