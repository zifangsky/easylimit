package cn.zifangsky.easylimit.enums;

/**
 * 项目模式的枚举
 *
 * @author zifangsky
 * @date 2019/5/7
 * @since 1.0.0
 */
public enum ProjectModeEnums {
    /**
     * Token
     */
    TOKEN("token"),
    /**
     * 默认
     */
    DEFAULT("default"),
    ;

    /**
     * CODE
     */
    private String code;

    ProjectModeEnums(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ProjectModeEnums fromCode(String code){
        if(code != null){
            for(ProjectModeEnums e : values()){
                if(e.getCode().equals(code)){
                    return e;
                }
            }
        }

        return null;
    }
}
