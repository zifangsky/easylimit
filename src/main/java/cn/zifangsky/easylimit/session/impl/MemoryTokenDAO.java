package cn.zifangsky.easylimit.session.impl;

import cn.zifangsky.easylimit.authc.PrincipalInfo;
import cn.zifangsky.easylimit.session.TokenDAO;
import cn.zifangsky.easylimit.session.impl.support.SimpleAccessToken;
import cn.zifangsky.easylimit.session.impl.support.SimpleRefreshToken;
import cn.zifangsky.easylimit.utils.DateUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用内存存储
 *
 * @author zifangsky
 * @date 2019/6/3
 * @since 1.0.0
 */
public class MemoryTokenDAO implements TokenDAO{

    /**
     * 使用{@link ConcurrentHashMap}存储所有{@link SimpleAccessToken}
     */
    private ConcurrentHashMap<String, SimpleAccessToken> accessTokenStorageMap;

    /**
     * 使用{@link ConcurrentHashMap}存储所有{@link SimpleRefreshToken}
     */
    private ConcurrentHashMap<String, SimpleRefreshToken> refreshTokenStorageMap;

    public MemoryTokenDAO() {
        this.accessTokenStorageMap = new ConcurrentHashMap<>();
        this.refreshTokenStorageMap = new ConcurrentHashMap<>();
    }

    public MemoryTokenDAO(ConcurrentHashMap<String, SimpleAccessToken> accessTokenStorageMap, ConcurrentHashMap<String, SimpleRefreshToken> refreshTokenStorageMap) {
        this.accessTokenStorageMap = accessTokenStorageMap;
        this.refreshTokenStorageMap = refreshTokenStorageMap;
    }

    @Override
    public SimpleAccessToken readByAccessToken(String accessToken) {
        return this.accessTokenStorageMap.get(accessToken);
    }

    @Override
    public SimpleRefreshToken readByRefreshToken(String refreshToken) {
        return this.refreshTokenStorageMap.get(refreshToken);
    }

    @Override
    public void updateAccessToken(SimpleAccessToken accessToken) {
        if(accessToken == null || accessToken.getAccessToken() == null){
            throw new IllegalArgumentException("Parameter accessToken cannot be empty.");
        }

        //设置最新访问时间
        accessToken.setLatestAccessTime(DateUtils.now());

        this.accessTokenStorageMap.put(accessToken.getAccessToken(), accessToken);
    }

    @Override
    public void updateRefreshToken(SimpleRefreshToken refreshToken) {
        if(refreshToken == null || refreshToken.getRefreshToken() == null){
            throw new IllegalArgumentException("Parameter refreshToken cannot be empty.");
        }

        //设置最新访问时间
        refreshToken.setLatestAccessTime(DateUtils.now());

        this.refreshTokenStorageMap.put(refreshToken.getRefreshToken(), refreshToken);
    }

    @Override
    public void deleteAccessToken(String accessToken) {
        if(accessToken == null){
            throw new IllegalArgumentException("Parameter accessToken cannot be empty.");
        }

        this.accessTokenStorageMap.remove(accessToken);
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        if(refreshToken == null){
            throw new IllegalArgumentException("Parameter refreshToken cannot be empty.");
        }

        this.refreshTokenStorageMap.remove(refreshToken);
    }

    @Override
    public void deleteOldAccessToken(String account) {
        if(account != null && this.accessTokenStorageMap != null){
            this.accessTokenStorageMap.forEach((key, value) -> {
                PrincipalInfo principalInfo = value.getPrincipalInfo();
                //删除该用户的历史Access Token
                if(principalInfo != null && account.equals(principalInfo.getAccount())){
                    this.accessTokenStorageMap.remove(key);
                }
            });
        }
    }

    @Override
    public void deleteOldRefreshToken(String accessToken) {
        if(accessToken != null && this.refreshTokenStorageMap != null){
            this.refreshTokenStorageMap.forEach((key, value) -> {
                //删除某个Access Token关联的所有Refresh Token
                if(accessToken.equals(value.getAccessToken())){
                    this.refreshTokenStorageMap.remove(key);
                }
            });
        }
    }
}
