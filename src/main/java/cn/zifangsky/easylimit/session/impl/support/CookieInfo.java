package cn.zifangsky.easylimit.session.impl.support;

import cn.zifangsky.easylimit.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * cookie基本信息
 *
 * @author zifangsky
 * @date 2019/4/12
 * @since 1.0.0
 */
public class CookieInfo {
    /**
     * Root路径
     */
    public static final String ROOT_PATH = "/";

    /**
     * cookie名称
     */
    private String name;
    /**
     * cookie值
     */
    private String value;
    /**
     * cookie所属的子域
     */
    private String domain;
    /**
     * 设置cookie路径
     */
    private String path;
    /**
     * 设置cookie的最大生存期
     */
    private int maxAge;
    /**
     * 是否只允许HTTPS访问
     */
    private boolean secure;
    /**
     * 是否将cookie设置成HttpOnly
     */
    private boolean httpOnly;

    public CookieInfo(String name) {
        this(name, true);
    }

    public CookieInfo(String name, boolean httpOnly) {
        this(name, null, CookieUtils.COOKIE_MAX_AGE, false, httpOnly);
    }

    public CookieInfo(String name, String domain, int maxAge, boolean secure, boolean httpOnly) {
        this(name, null, domain, null, maxAge, secure, httpOnly);
    }

    public CookieInfo(String name, String value, String domain, String path, int maxAge, boolean secure, boolean httpOnly) {
        this.name = name;
        this.value = value;
        this.domain = domain;
        this.path = path;
        this.maxAge = maxAge;
        this.secure = secure;
        this.httpOnly = httpOnly;
    }

    /**
     * 自动计算Path
     */
    public String calculatePath(HttpServletRequest request) {
        String path = this.path;
        if (StringUtils.isBlank(path)) {
            path = StringUtils.trimToNull(request.getContextPath());
        }

        if (path == null) {
            path = ROOT_PATH;
        }

        return path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    @Override
    public String toString() {
        return "CookieInfo{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", domain='" + domain + '\'' +
                ", path='" + path + '\'' +
                ", maxAge=" + maxAge +
                ", secure=" + secure +
                ", httpOnly=" + httpOnly +
                '}';
    }
}
