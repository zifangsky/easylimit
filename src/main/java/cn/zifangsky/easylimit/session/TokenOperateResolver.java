package cn.zifangsky.easylimit.session;

import cn.zifangsky.easylimit.authc.PrincipalInfo;
import cn.zifangsky.easylimit.authc.ValidatedInfo;
import cn.zifangsky.easylimit.session.impl.support.SimpleAccessToken;
import cn.zifangsky.easylimit.session.impl.support.SimpleRefreshToken;
import cn.zifangsky.easylimit.session.impl.support.TokenInfo;

import java.io.Serializable;

/**
 * Token的几种基本操作
 *
 * @author zifangsky
 * @date 2019/6/4
 * @since 1.0.0
 */
public interface TokenOperateResolver {

    /**
     * 创建Access Token
     * @author zifangsky
     * @date 2019/6/4 16:42
     * @since 1.0.0
     * @param principalInfo 用户主体
     * @param tokenInfo 用于获取有效期
     * @param sessionId sessionId
     * @return cn.zifangsky.easylimit.session.impl.support.SimpleAccessToken
     */
    SimpleAccessToken createAccessToken(PrincipalInfo principalInfo, TokenInfo tokenInfo, Serializable sessionId);

    /**
     * 创建Refresh Token
     * @author zifangsky
     * @date 2019/6/4 16:42
     * @since 1.0.0
     * @param validatedInfo 登录信息
     * @param tokenInfo 用于获取有效期
     * @param accessToken Access Token
     * @return cn.zifangsky.easylimit.session.impl.support.SimpleRefreshToken
     */
    SimpleRefreshToken createRefreshToken(ValidatedInfo validatedInfo, TokenInfo tokenInfo, String accessToken);

    /**
     * 校验某个Access Token是否仍然有效
     * @author zifangsky
     * @date 2019/6/4 14:12
     * @since 1.0.0
     * @param simpleAccessToken Access Token
     * @return boolean
     */
    boolean isValid(SimpleAccessToken simpleAccessToken);

    /**
     * 校验某个Refresh Token是否仍然有效
     * @author zifangsky
     * @date 2019/6/4 14:12
     * @since 1.0.0
     * @param simpleRefreshToken Refresh Token
     * @return boolean
     */
    boolean isValid(SimpleRefreshToken simpleRefreshToken);
}
