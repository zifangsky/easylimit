package cn.zifangsky.easylimit.filter.impl.support;

import cn.zifangsky.easylimit.enums.DefaultTokenRespEnums;

/**
 * 基于Token的返回提示
 *
 * @author zifangsky
 * @date 2019/5/7
 * @since 1.0.0
 */
public class TokenRespMsg {
    /**
     * 返回状态码
     */
    private Integer code;

    /**
     * 返回字段名
     */
    private String name;

    /**
     * 返回提示信息
     */
    private String msg;

    public TokenRespMsg() {
    }

    public TokenRespMsg(DefaultTokenRespEnums defaultTokenRespEnums) {
        this.code = defaultTokenRespEnums.getCode();
        this.name = defaultTokenRespEnums.getName();
        this.msg = defaultTokenRespEnums.getMsg();
    }

    public TokenRespMsg(Integer code, String name, String msg) {
        this.code = code;
        this.name = name;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TokenRespMsg{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
