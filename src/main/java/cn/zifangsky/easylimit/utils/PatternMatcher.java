package cn.zifangsky.easylimit.utils;

/**
 * 使用正则表达式匹配路径
 * @author zifangsky
 * @date 2019/4/26
 * @since 1.0.0
 */
public interface PatternMatcher {

    /**
     * 判断给定的路径是否匹配特定路径模式
     * @author zifangsky
     * @date 2019/4/26 13:40
     * @since 1.0.0
     * @param pattern 特定路径模式
     * @param path 原路径
     * @return boolean
     */
    boolean match(String pattern, String path);
}
