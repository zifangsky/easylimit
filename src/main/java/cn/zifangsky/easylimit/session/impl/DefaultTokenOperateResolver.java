package cn.zifangsky.easylimit.session.impl;

import cn.zifangsky.easylimit.authc.PrincipalInfo;
import cn.zifangsky.easylimit.authc.ValidatedInfo;
import cn.zifangsky.easylimit.session.TokenOperateResolver;
import cn.zifangsky.easylimit.session.impl.support.SimpleAccessToken;
import cn.zifangsky.easylimit.session.impl.support.SimpleRefreshToken;
import cn.zifangsky.easylimit.session.impl.support.TokenInfo;
import cn.zifangsky.easylimit.utils.DateUtils;
import cn.zifangsky.easylimit.utils.EncryptUtils;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 默认实现
 *
 * @author zifangsky
 * @date 2019/6/4
 * @since 1.0.0
 */
public class DefaultTokenOperateResolver implements TokenOperateResolver{

    @Override
    public SimpleAccessToken createAccessToken(PrincipalInfo principalInfo, TokenInfo tokenInfo, Serializable sessionId) {
        String account = principalInfo.getAccount();
        LocalDateTime now = DateUtils.now();
        //过期时间
        LocalDateTime expirationTime = now.plus(tokenInfo.getAccessTokenTimeout(), tokenInfo.getAccessTokenTimeoutUnit());
        //剩余秒数
        long expiresIn = Duration.between(now, expirationTime).getSeconds();

        //1. 拼装待加密字符串（account + 当前精确到毫秒的时间戳）
        String str = account + String.valueOf(DateUtils.nowMilliSecondTimestamp(null));

        //2. SHA1加密
        String accessTokenStr = "1." + EncryptUtils.sha1Hex(str) + "." + expiresIn + "." + DateUtils.getSecondTimestamp(expirationTime, null);

        //3. 返回对象
        return new SimpleAccessToken(accessTokenStr, expiresIn, principalInfo, sessionId, now);
    }

    @Override
    public SimpleRefreshToken createRefreshToken(ValidatedInfo validatedInfo, TokenInfo tokenInfo, String accessToken) {
        String account = validatedInfo.getSubject();
        LocalDateTime now = DateUtils.now();
        //过期时间
        LocalDateTime expirationTime = now.plus(tokenInfo.getRefreshTokenTimeout(), tokenInfo.getRefreshTokenTimeoutUnit());
        //剩余秒数
        long expiresIn = Duration.between(now, expirationTime).getSeconds();

        //1. 拼装待加密字符串（account + accessToken +当前精确到毫秒的时间戳）
        String str = account + accessToken + String.valueOf(DateUtils.nowMilliSecondTimestamp(null));

        //2. SHA1加密
        String refreshTokenStr = "2." + EncryptUtils.sha1Hex(str) + "." + expiresIn + "." + DateUtils.getSecondTimestamp(expirationTime, null);

        //3. 返回对象
        return new SimpleRefreshToken(refreshTokenStr, expiresIn, accessToken, validatedInfo, now);
    }

    @Override
    public boolean isValid(SimpleAccessToken simpleAccessToken) {
        if(simpleAccessToken == null){
            return false;
        }else{
            LocalDateTime now = DateUtils.now();
            //过期时间
            LocalDateTime expirationTime = simpleAccessToken.getCreateTime().plus(simpleAccessToken.getExpiresIn(), ChronoUnit.SECONDS);

            return now.isBefore(expirationTime);
        }
    }

    @Override
    public boolean isValid(SimpleRefreshToken simpleRefreshToken) {
        if(simpleRefreshToken == null){
            return false;
        }else{
            LocalDateTime now = DateUtils.now();
            //过期时间
            LocalDateTime expirationTime = simpleRefreshToken.getCreateTime().plus(simpleRefreshToken.getExpiresIn(), ChronoUnit.SECONDS);

            return now.isBefore(expirationTime);
        }
    }
}
