package love.vincentcorleone.easypan.entity.po;

import java.util.Date;

public class Share {
    private long id;
    private long userId;
    private String viewDir;
    private String fileName;
    private Date shareTime;
    private Date expireTime;
    private boolean isForever;

    private int viewTimes;
    private String linkSuffix;
    private String code;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
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

    public Date getShareTime() {
        return shareTime;
    }

    public void setShareTime(Date shareTime) {
        this.shareTime = shareTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public boolean isForever() {
        return isForever;
    }

    public void setForever(boolean forever) {
        isForever = forever;
    }

    public int getViewTimes() {
        return viewTimes;
    }

    public void setViewTimes(int viewTimes) {
        this.viewTimes = viewTimes;
    }

    public String getLinkSuffix() {
        return linkSuffix;
    }

    public void setLinkSuffix(String linkSuffix) {
        this.linkSuffix = linkSuffix;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
