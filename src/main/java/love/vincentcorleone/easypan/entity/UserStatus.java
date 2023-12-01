package love.vincentcorleone.easypan.entity;

public enum UserStatus {
    Enabled(0,"启用"),
    Disabled(1,"禁用");

    // 成员变量
    private String name;
    private int code;
    private UserStatus(int code, String name) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
