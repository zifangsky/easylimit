package cn.zifangsky.easylimit.authc.impl;

import cn.zifangsky.easylimit.authc.PrincipalInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * {@link PrincipalInfo}的基本实现
 * <p>Note: 登录校验时正确的用户名、密码</p>
 *
 * @author zifangsky
 * @date 2019/4/3
 * @since 1.0.0
 */
public class SimplePrincipalInfo implements PrincipalInfo {
    private static final long serialVersionUID = 8416222905146081056L;

    /**
     * 用户主体
     */
    private Object principal;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    public SimplePrincipalInfo() {

    }

    public SimplePrincipalInfo(Object principal, String username, String password) {
        this.principal = principal;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    @JsonIgnore
    public String getAccount() {
        return this.username;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public void setPrincipal(Object principal) {
        this.principal = principal;
    }

    @Override
    public String toString() {
        return "SimplePrincipalInfo{" +
                ", username='" + username + '\'' +
                ", password='it doesn't show up here'" + '\'' +
                '}';
    }
}
