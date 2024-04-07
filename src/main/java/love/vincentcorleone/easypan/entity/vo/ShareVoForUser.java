package love.vincentcorleone.easypan.entity.vo;

import love.vincentcorleone.easypan.entity.po.Share;

import java.text.SimpleDateFormat;

public class ShareVoForUser {

    public ShareVoForUser(Share share){
        this.fileName = share.getFileName();
        this.linkSuffix = share.getLinkSuffix();

        String pattern1 = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf1 = new SimpleDateFormat(pattern1);
        this.shareTime = sdf1.format(share.getShareTime());
        if(share.getExpireTime() == null){
            this.expireTime = "永久";
        }else{
            this.expireTime = sdf1.format(share.getExpireTime());
        }

    }

    private String fileName;

    private String linkSuffix;

    private String shareTime;

    private String expireTime;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLinkSuffix() {
        return linkSuffix;
    }

    public void setLinkSuffix(String linkSuffix) {
        this.linkSuffix = linkSuffix;
    }

    public String getShareTime() {
        return shareTime;
    }

    public void setShareTime(String shareTime) {
        this.shareTime = shareTime;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }
}
