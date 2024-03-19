package love.vincentcorleone.easypan.entity.vo;

import love.vincentcorleone.easypan.entity.po.Share;

public class ShareVo {

    public ShareVo(Share share){
        this.code = share.getCode();
        this.linkSuffix = share.getLinkSuffix();
    }

    private String link;

    private String linkSuffix;

    private String code;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
