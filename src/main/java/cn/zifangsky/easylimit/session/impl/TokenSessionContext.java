package cn.zifangsky.easylimit.session.impl;

import cn.zifangsky.easylimit.session.SessionContext;
import cn.zifangsky.easylimit.session.impl.support.SimpleAccessToken;
import cn.zifangsky.easylimit.session.impl.support.SimpleRefreshToken;

import java.io.Serializable;
import java.util.Map;

/**
 * 基于Token模式的{@link SessionContext}
 *
 * @author zifangsky
 * @date 2019/6/3
 * @see SessionContext
 * @since 1.0.0
 */
public class TokenSessionContext extends DefaultSessionContext {
    private static final long serialVersionUID = 6297415889146147706L;

    /**
     * {@link SimpleAccessToken}在session中的key
     */
    public static final String SIMPLE_ACCESS_TOKEN_KEY = TokenSessionContext.class.getName() + ":simple_access_token";

    /**
     * {@link SimpleRefreshToken}在session中的key
     */
    public static final String SIMPLE_REFRESH_TOKEN_KEY = TokenSessionContext.class.getName() + ":simple_refresh_token";


    public TokenSessionContext() {
        this(null, null);
    }

    public TokenSessionContext(String host) {
        this(host, null);
    }

    public TokenSessionContext(String host, Serializable sessionId) {
        super();

        if(host != null){
            this.setHost(host);
        }
        if(sessionId != null){
            this.setSessionId(sessionId);
        }
    }

    public TokenSessionContext(Map<String, Object> map) {
        super(map);
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
