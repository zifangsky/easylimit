package cn.zifangsky.easylimit.session.impl.support;

import cn.zifangsky.easylimit.authc.PrincipalInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Access Token实例
 *
 * @author zifangsky
 * @date 2019/6/3
 * @since 1.0.0
 */
public class SimpleAccessToken {
    /**
     * Access Token
     */
    private String accessToken;

    /**
     * 过期时间（单位为秒）
     */
    private Long expiresIn;

    /**
     * 关联的用户主体信息
     */
    private PrincipalInfo principalInfo;

    /**
     * 关联的会话ID
     */
    private Serializable sessionId;

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

    public SimpleAccessToken() {

    }

    public SimpleAccessToken(String accessToken, Long expiresIn, PrincipalInfo principalInfo, Serializable sessionId, LocalDateTime createTime) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.principalInfo = principalInfo;
        this.sessionId = sessionId;

        this.createTime = createTime;
        this.latestAccessTime = createTime;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public PrincipalInfo getPrincipalInfo() {
        return principalInfo;
    }

    public void setPrincipalInfo(PrincipalInfo principalInfo) {
        this.principalInfo = principalInfo;
    }

    public Serializable getSessionId() {
        return sessionId;
    }

    public void setSessionId(Serializable sessionId) {
        this.sessionId = sessionId;
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

    @Override
    public String toString() {
        return "SimpleAccessToken{" +
                "accessToken='" + accessToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", principalInfo=" + principalInfo +
                ", sessionId=" + sessionId +
                ", createTime=" + createTime +
                ", latestAccessTime=" + latestAccessTime +
                ", stopTime=" + stopTime +
                '}';
    }
}
