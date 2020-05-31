package cn.zifangsky.easylimit.access.impl;

import cn.zifangsky.easylimit.access.AccessContext;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.session.impl.TokenSessionContext;
import cn.zifangsky.easylimit.session.impl.support.SimpleAccessToken;
import cn.zifangsky.easylimit.session.impl.support.SimpleRefreshToken;

/**
 * 基于token模式的{@link AccessContext}
 *
 * @author zifangsky
 * @date 2019/6/4
 * @since 1.0.0
 */
public class TokenAccessContext extends DefaultAccessContext {
    private static final long serialVersionUID = 6748814370152609571L;

    /**
     * {@link SimpleAccessToken}的key
     */
    public static final String SIMPLE_ACCESS_TOKEN_KEY = TokenAccessContext.class.getName() + ":simple_access_token";

    /**
     * {@link SimpleRefreshToken}的key
     */
    public static final String SIMPLE_REFRESH_TOKEN_KEY = TokenAccessContext.class.getName() + ":simple_refresh_token";

    public SimpleAccessToken acquireAccessToken() {
        SimpleAccessToken accessToken = this.getSimpleAccessToken();

        //如果为空，尝试从session获取
        if(accessToken == null){
            Session session = this.acquireSession();

            if(session != null){
                accessToken = (SimpleAccessToken) session.getAttribute(TokenSessionContext.SIMPLE_ACCESS_TOKEN_KEY);
            }
        }

        return accessToken;
    }

    public SimpleRefreshToken acquireRefreshToken() {
        SimpleRefreshToken refreshToken = this.getSimpleRefreshToken();

        //如果为空，尝试从session获取
        if(refreshToken == null){
            Session session = this.acquireSession();

            if(session != null){
                refreshToken = (SimpleRefreshToken) session.getAttribute(TokenSessionContext.SIMPLE_REFRESH_TOKEN_KEY);
            }
        }

        return refreshToken;
    }

    public SimpleAccessToken getSimpleAccessToken() {
        return getByType(SIMPLE_ACCESS_TOKEN_KEY, SimpleAccessToken.class);
    }

    public void setSimpleAccessToken(SimpleAccessToken accessToken) {
        put(SIMPLE_ACCESS_TOKEN_KEY, accessToken);
    }

    public SimpleRefreshToken getSimpleRefreshToken() {
        return getByType(SIMPLE_REFRESH_TOKEN_KEY, SimpleRefreshToken.class);
    }

    public void setSimpleRefreshToken(SimpleRefreshToken refreshToken) {
        put(SIMPLE_REFRESH_TOKEN_KEY, refreshToken);
    }
}
