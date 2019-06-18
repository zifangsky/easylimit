package cn.zifangsky.easylimit.session.impl.support;

/**
 * 包含Access Token和Refresh Token
 *
 * @author zifangsky
 * @date 2019/6/13
 * @since 1.0.0
 */
public class SimpleAccessRefreshToken {
    /**
     * Access Token
     */
    private SimpleAccessToken accessToken;

    /**
     * Refresh Token
     */
    private SimpleRefreshToken refreshToken;

    public SimpleAccessRefreshToken() {
    }

    public SimpleAccessRefreshToken(SimpleAccessToken accessToken, SimpleRefreshToken refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public SimpleAccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(SimpleAccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public SimpleRefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(SimpleRefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "SimpleAccessRefreshToken{" +
                "accessToken=" + accessToken +
                ", refreshToken=" + refreshToken +
                '}';
    }
}
