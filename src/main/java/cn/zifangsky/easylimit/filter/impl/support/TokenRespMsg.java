package cn.zifangsky.easylimit.filter.impl.support;

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
     * 返回提示信息
     */
    private String msg;

    public TokenRespMsg() {
    }

    public TokenRespMsg(Integer code, String msg) {
        this.code = code;
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

    @Override
    public String toString() {
        return "TokenErrorRespMsg{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
