package cn.zifangsky.easylimit.session;

import cn.zifangsky.easylimit.session.impl.support.SimpleAccessToken;
import cn.zifangsky.easylimit.session.impl.support.SimpleRefreshToken;

/**
 * token存储
 *
 * @author zifangsky
 * @date 2019/6/3
 * @since 1.0.0
 */
public interface TokenDAO {
    /**
     * 通过Access Token获取{@link SimpleAccessToken}
     * @author zifangsky
     * @date 2019/6/3 14:57
     * @since 1.0.0
     * @param accessToken Access Token
     * @return cn.zifangsky.easylimit.session.impl.support.SimpleAccessToken
     */
    SimpleAccessToken readByAccessToken(String accessToken);

    /**
     * 通Refresh Token获取{@link SimpleRefreshToken}
     * @author zifangsky
     * @date 2019/6/3 14:57
     * @since 1.0.0
     * @param refreshToken Refresh Token
     * @return cn.zifangsky.easylimit.session.impl.support.SimpleAccessToken
     */
    SimpleRefreshToken readByRefreshToken(String refreshToken);

    /**
     * 更新Access Token
     * @author zifangsky
     * @date 2019/6/3 15:02
     * @since 1.0.0
     * @param simpleAccessToken Access Token实例
     */
    void updateAccessToken(SimpleAccessToken simpleAccessToken);

    /**
     * 更新Refresh Token
     * @author zifangsky
     * @date 2019/6/3 15:02
     * @since 1.0.0
     * @param refreshToken Refresh Token实例
     */
    void updateRefreshToken(SimpleRefreshToken refreshToken);

    /**
     * 删除指定的Access Token
     * @author zifangsky
     * @date 2019/6/3 15:06
     * @since 1.0.0
     * @param accessToken Access Token
     */
    void deleteAccessToken(String accessToken);

    /**
     * 删除指定的Refresh Token
     * @author zifangsky
     * @date 2019/6/3 15:06
     * @since 1.0.0
     * @param refreshToken Refresh Token
     */
    void deleteRefreshToken(String refreshToken);

    /**
     * 删除某个用户的所有历史Access Token
     * @author zifangsky
     * @date 2019/6/14 17:18
     * @since 1.0.0
     * @param account 账户标识
     */
    void deleteOldAccessToken(String account);

    /**
     * 删除某个用户的所有历史Refresh Token
     * @author zifangsky
     * @date 2019/6/14 17:20
     * @since 1.0.0
     * @param accessToken Access Token
     */
    void deleteOldRefreshToken(String accessToken);
}
