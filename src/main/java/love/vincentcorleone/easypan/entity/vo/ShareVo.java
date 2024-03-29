package love.vincentcorleone.easypan.entity.vo;

import love.vincentcorleone.easypan.entity.po.Share;

public class ShareVo {

    public ShareVo(Share share){
        this.linkSuffix = share.getLinkSuffix();
    }

    private String linkSuffix;

    public String getLinkSuffix() {
        return linkSuffix;
    }

    public void setLinkSuffix(String linkSuffix) {
        this.linkSuffix = linkSuffix;
    }

}
