package love.vincentcorleone.easypan.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("code2path")
public class Code2Path {
    private String code;

    private String path;
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
