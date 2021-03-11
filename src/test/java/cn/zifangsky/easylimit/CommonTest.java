package cn.zifangsky.easylimit;

import cn.zifangsky.easylimit.session.impl.DefaultSessionContext;
import cn.zifangsky.easylimit.utils.AntPathMatcher;
import org.junit.jupiter.api.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zifangsky
 * @date 2019/3/25
 * @since 1.0.0
 */
@DisplayName("基本测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommonTest {

    @Test
    @Order(1)
    @DisplayName("DefaultSessionContext")
    public void test1(){
        String key = DefaultSessionContext.class.getName() + ":HOST";

        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put(key, "127.0.0.1");

        System.out.println(map.get(key));
    }

    @Test
    @Order(2)
    @DisplayName("ConcurrentHashMap")
    public void test2(){
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        map.put("112", "aaa");
        map.put("223", "bbb");
        map.remove("456");
        System.out.println(map.size());

    }

    @Test
    @Order(3)
    @DisplayName("ConcurrentHashMap")
    public void test3(){
        ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> cacheMap = new ConcurrentHashMap<>(16);

        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        map.put("112", "aaa");
        map.put("223", "bbb");
        cacheMap.put("session", map);

        ConcurrentHashMap<String, Object> tmp = cacheMap.get("session");
        tmp.put("abc", "abc");
        tmp.remove("223");

        System.out.println(cacheMap.size());
    }

    @Test
    @Order(4)
    @DisplayName("解析URL")
    public void test4(){
        String redirectUrl = "http://example.com/test/aaa?id=5#point";

        String anchor = null;
        int anchorIndex = redirectUrl.indexOf('#');
        if (anchorIndex > -1) {
            anchor = redirectUrl.substring(anchorIndex);
            redirectUrl = redirectUrl.substring(0, anchorIndex);
        }

        System.out.println(redirectUrl);
    }

    @Test
    @Order(5)
    @DisplayName("匹配URL")
    public void test5(){
        AntPathMatcher pathMatcher = new AntPathMatcher();

        System.out.println(pathMatcher.match("/x/x/**/abc", "/x/x/x/abc"));
        System.out.println(pathMatcher.match("/css/**", "/css/a/b/c/"));
        System.out.println(pathMatcher.match("/**", "/a/b"));
    }


}
