package cn.zifangsky.easylimit.session.impl.support;

import cn.zifangsky.easylimit.authc.ValidatedInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Refresh Token实例
 *
 * @author zifangsky
 * @date 2019/6/3
 * @since 1.0.0
 */
public class SimpleRefreshToken {
    /**
     * Refresh Token
     */
    private String refreshToken;

    /**
     * 过期时间（单位为秒）
     */
    private Long expiresIn;

    /**
     * 关联的Access Token
     */
    private String accessToken;

    /**
     * 关联的登录信息
     */
    private ValidatedInfo validatedInfo;

    /**
     * 是否已经过期
     */
    private boolean expired = false;

    /**
     * 创建时间
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 最新访问时间
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime latestAccessTime;

    /**
     * 停用时间
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime stopTime;

    public SimpleRefreshToken() {

    }

    public SimpleRefreshToken(String refreshToken, Long expiresIn, String accessToken, ValidatedInfo validatedInfo, LocalDateTime createTime) {
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.accessToken = accessToken;
        this.validatedInfo = validatedInfo;

        this.createTime = createTime;
        this.latestAccessTime = createTime;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ValidatedInfo getValidatedInfo() {
        return validatedInfo;
    }

    public void setValidatedInfo(ValidatedInfo validatedInfo) {
        this.validatedInfo = validatedInfo;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getLatestAccessTime() {
        return latestAccessTime;
    }

    public void setLatestAccessTime(LocalDateTime latestAccessTime) {
        this.latestAccessTime = latestAccessTime;
    }

    public LocalDateTime getStopTime() {
        return stopTime;
    }

    public void setStopTime(LocalDateTime stopTime) {
        this.stopTime = stopTime;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    @Override
    public String toString() {
        return "SimpleRefreshToken{" +
                "refreshToken='" + refreshToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", accessToken='" + accessToken + '\'' +
                ", validatedInfo=" + validatedInfo +
                ", expired=" + expired +
                ", createTime=" + createTime +
                ", latestAccessTime=" + latestAccessTime +
                ", stopTime=" + stopTime +
                '}';
    }
}
