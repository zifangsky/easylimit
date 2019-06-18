package cn.zifangsky.easylimit.enums;

/**
 * 默认的几种基于Token的返回提示
 *
 * @author zifangsky
 * @date 2019/5/7
 * @since 1.0.0
 */
public enum DefaultTokenRespEnums {
    /**
     * 登录成功
     */
    LOGIN_SUCCESS(200, "登录成功！"),
    /**
     * 注销登录成功
     */
    LOGOUT(200, "注销登录成功！"),

    /**
     * 登录失败
     */
    LOGIN_FAILURE(401, "登录失败！"),

    /**
     * 未登录
     */
    UN_LOGIN(401, "您还未登录系统，无法访问该地址！"),
    /**
     * 被踢出
     */
    KICKOUT(401, "您的账号已在其他设备登录，若非本人操作，请立即重新登录并修改密码！"),
    /**
     * 没有权限
     */
    NO_PERMISSIONS(403, "您当前没有权限访问该地址！"),
    /**
     * 不可用的TOKEN
     */
    INVALID_TOKEN(401,"请求的Access Token或Refresh Token不可用！"),
    /**
     * TOKEN过期
     */
    EXPIRED_TOKEN(403,"请求的Access Token或Refresh Token已过期！")
    ;

    DefaultTokenRespEnums(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 返回状态码
     */
    private int code;

    /**
     * 返回提示信息
     */
    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
