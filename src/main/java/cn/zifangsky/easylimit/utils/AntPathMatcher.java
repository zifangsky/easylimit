package cn.zifangsky.easylimit.utils;

public class AntPathMatcher implements PatternMatcher {

    /**
     * 这里的逻辑调用{@link org.springframework.util.AntPathMatcher}实现
     */
    private org.springframework.util.PathMatcher springPathMatcher;

    public AntPathMatcher() {
        this.springPathMatcher = new org.springframework.util.AntPathMatcher();
    }

    @Override
    public boolean match(String pattern, String path) {
        return springPathMatcher.match(pattern, path);
    }

}
