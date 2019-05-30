package cn.zifangsky.easylimit.authc.impl;

import cn.zifangsky.easylimit.authc.ValidatedInfo;

/**
 * 基于“手机号码+短信验证码”的登录验证信息
 *
 * @author zifangsky
 * @date 2019/4/3
 * @since 1.0.0
 */
public class PhoneCodeValidatedInfo implements ValidatedInfo {
    private static final long serialVersionUID = 5329929348382496608L;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 短信验证码
     */
    private String code;

    public PhoneCodeValidatedInfo(String phone, String code) {
        this.phone = phone;
        this.code = code;
    }

    @Override
    public String getSubject() {
        return this.phone;
    }

    @Override
    public String getCredentials() {
        return this.code;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "PhoneCodeValidatedInfo{" +
                "phone='" + phone + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
