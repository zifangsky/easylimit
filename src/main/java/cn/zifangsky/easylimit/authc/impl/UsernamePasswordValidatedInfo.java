package cn.zifangsky.easylimit.authc.impl;

import cn.zifangsky.easylimit.authc.ValidatedInfo;
import cn.zifangsky.easylimit.enums.EncryptionTypeEnums;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 基于“用户名+密码”的登录验证信息
 *
 * @author zifangsky
 * @date 2019/4/3
 * @since 1.0.0
 */
public class UsernamePasswordValidatedInfo implements ValidatedInfo {
    private static final long serialVersionUID = 5329929348382496608L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 密码的加密方式
     */
    private EncryptionTypeEnums encryptionType;

    public UsernamePasswordValidatedInfo() {
    }

    public UsernamePasswordValidatedInfo(String username, String password, EncryptionTypeEnums encryptionType) {
        this.username = username;
        this.password = password;
        this.encryptionType = encryptionType;
    }

    @Override
    @JsonIgnore
    public String getSubject() {
        return this.username;
    }

    @Override
    @JsonIgnore
    public String getCredentials() {
        return this.password;
    }

    public EncryptionTypeEnums getEncryptionType() {
        return encryptionType;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEncryptionType(EncryptionTypeEnums encryptionType) {
        this.encryptionType = encryptionType;
    }

    @Override
    public String toString() {
        return "UsernamePasswordValidatedInfo{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", encryptionType=" + encryptionType +
                '}';
    }
}
