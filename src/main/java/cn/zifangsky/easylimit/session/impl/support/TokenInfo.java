package cn.zifangsky.easylimit.session.impl.support;

import cn.zifangsky.easylimit.common.Constants;
import cn.zifangsky.easylimit.enums.DefaultTimeEnums;

import java.time.temporal.ChronoUnit;

/**
 * Token模式的参数设置
 *
 * @author zifangsky
 * @date 2019/5/31
 * @since 1.0.0
 */
public class TokenInfo {
    /**
     * Access Token的参数名
     */
    private String accessTokenParamName;

    /**
     * Refresh Token的参数名
     */
    private String refreshTokenParamName;

    /**
     * Access Token的过期时间的参数名
     */
    private String expiresInParamName;

    /**
     * Access Token的超时时间
     */
    private Long accessTokenTimeout;
    /**
     * Access Token的超时时间的单位
     */
    private ChronoUnit accessTokenTimeoutUnit;

    /**
     * Refresh Token的超时时间
     */
    private Long refreshTokenTimeout;
    /**
     * Refresh Token的超时时间的单位
     */
    private ChronoUnit refreshTokenTimeoutUnit;

    public TokenInfo() {
        this.accessTokenParamName = Constants.DEFAULT_ACCESS_TOKEN_PARAM_NAME;
        this.refreshTokenParamName = Constants.DEFAULT_REFRESH_TOKEN_PARAM_NAME;
        this.expiresInParamName = Constants.DEFAULT_EXPIRES_IN_PARAM_NAME;
        this.accessTokenTimeout = DefaultTimeEnums.ACCESS_TOKEN.getTime();
        this.accessTokenTimeoutUnit = DefaultTimeEnums.ACCESS_TOKEN.getChronoUnit();
        this.refreshTokenTimeout = DefaultTimeEnums.REFRESH_TOKEN.getTime();
        this.refreshTokenTimeoutUnit = DefaultTimeEnums.REFRESH_TOKEN.getChronoUnit();
    }

    public String getAccessTokenParamName() {
        return accessTokenParamName;
    }

    public void setAccessTokenParamName(String accessTokenParamName) {
        this.accessTokenParamName = accessTokenParamName;
    }

    public String getRefreshTokenParamName() {
        return refreshTokenParamName;
    }

    public void setRefreshTokenParamName(String refreshTokenParamName) {
        this.refreshTokenParamName = refreshTokenParamName;
    }

    public String getExpiresInParamName() {
        return expiresInParamName;
    }

    public void setExpiresInParamName(String expiresInParamName) {
        this.expiresInParamName = expiresInParamName;
    }

    public Long getAccessTokenTimeout() {
        return accessTokenTimeout;
    }

    public void setAccessTokenTimeout(Long accessTokenTimeout) {
        this.accessTokenTimeout = accessTokenTimeout;
    }

    public ChronoUnit getAccessTokenTimeoutUnit() {
        return accessTokenTimeoutUnit;
    }

    public void setAccessTokenTimeoutUnit(ChronoUnit accessTokenTimeoutUnit) {
        this.accessTokenTimeoutUnit = accessTokenTimeoutUnit;
    }

    public Long getRefreshTokenTimeout() {
        return refreshTokenTimeout;
    }

    public void setRefreshTokenTimeout(Long refreshTokenTimeout) {
        this.refreshTokenTimeout = refreshTokenTimeout;
    }

    public ChronoUnit getRefreshTokenTimeoutUnit() {
        return refreshTokenTimeoutUnit;
    }

    public void setRefreshTokenTimeoutUnit(ChronoUnit refreshTokenTimeoutUnit) {
        this.refreshTokenTimeoutUnit = refreshTokenTimeoutUnit;
    }

    @Override
    public String toString() {
        return "TokenInfo{" +
                "accessTokenParamName='" + accessTokenParamName + '\'' +
                ", refreshTokenParamName='" + refreshTokenParamName + '\'' +
                ", expiresInParamName='" + expiresInParamName + '\'' +
                ", accessTokenTimeout=" + accessTokenTimeout +
                ", accessTokenTimeoutUnit=" + accessTokenTimeoutUnit +
                ", refreshTokenTimeout=" + refreshTokenTimeout +
                ", refreshTokenTimeoutUnit=" + refreshTokenTimeoutUnit +
                '}';
    }
}
