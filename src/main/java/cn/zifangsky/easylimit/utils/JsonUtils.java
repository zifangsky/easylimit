package cn.zifangsky.easylimit.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * JSON相关公共方法（通过Fastjson实现）
 *
 * @author zifangsky
 * @date 2017/5/2
 * @since 1.0.0
 */
public class JsonUtils {

    /**
     * 将对象转化为json字符串
     *
     * @param source Java对象
     * @return java.lang.String
     */
    public static <K> String toJson(K source) {
        return JSON.toJSON(source).toString();
    }

    /**
     * 将json字符串还原为目标对象
     *
     * @param source json字符串
     * @return K
     */
    public static <T> T fromJson(String source, Class<T> clazz) {
        return JSON.parseObject(source, clazz);
    }

    /**
     * 将数组类型的json字符串还原为目标对象
     *
     * @param source json字符串
     * @return java.util.List<T>
     */
    public static <T> List<T> fromArrayJson(String source, Class<T> clazz) {
        return JSON.parseArray(source, clazz);
    }

    /**
     * 将数组类型的json字符串还原为较为复杂的List<Map<String, Object>>格式
     *
     * @return java.util.List<java.util.Map   <   java.lang.String   ,   java.lang.Object>>
     */
    public static List<Map<String, Object>> fromArrayJson(String source) {
        return JSON.parseObject(source, new TypeReference<List<Map<String, Object>>>() {
        });
    }

}
