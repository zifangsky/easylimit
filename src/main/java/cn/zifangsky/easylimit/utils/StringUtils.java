package cn.zifangsky.easylimit.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串相关公共方法
 *
 * @author zifangsky
 * @date 2019/4/2
 * @since 1.0.0
 */
public class StringUtils {
    private static final String CAPITALS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 匹配形如 <b>login, roles[reviewer, subscriber], perms[list, edit]</b> 的字符串
     */
    private static final Pattern SPLIT_BY_COMMA_PATTERN = Pattern.compile("([^,\\f\\n\\r\\t\\v ]+\\[.+?\\])|([^,\\f\\n\\r\\t\\v ]+)");


    /**
     * 匹配形如 <b>roles[reviewer, subscriber]</b> 的字符串
     */
    private static final Pattern SPLIT_BY_SQUARE_BRACKETS_PATTERN = Pattern.compile("(\\S+)\\[(.+?)\\]");

    /**
     * 匹配形如 <b>login</b> 的字符串
     */
    private static final Pattern ANY_CHARACTER_PATTERN = Pattern.compile("\\S+");

    /**
     * 获取长度为num的随机数
     *
     * @param num 生成的字符串长度
     */
    public static String getRandomStr(final int num) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i <= num; i++) {
            stringBuilder.append(CAPITALS.charAt(new Random().nextInt(CAPITALS.length())));
        }
        return stringBuilder.toString();
    }

    /**
     * 将字符串通过中括号分割成两部分，示例如下：
     * <table>
     *     <tr>
     *         <th>输入</th>
     *         <th>输出</th>
     *     </tr>
     *
     *     <tr>
     *         <td>login</td>
     *         <td>result[0]: login <br/> result[1]: null</td>
     *     </tr>
     *
     *     <tr>
     *         <td>roles[reviewer, subscriber]</td>
     *         <td>result[0]: roles <br/> result[1]: reviewer, subscriber</td>
     *     </tr>
     *
     * </table>
     *
     * @author zifangsky
     * @date 2019/5/10 14:10
     * @since 1.0.0
     * @param source 原字符串
     * @return java.lang.String[]
     */
    public static String[] splitBySquareBrackets(String source){
        String[] result = new String[2];

        Matcher matcher = SPLIT_BY_SQUARE_BRACKETS_PATTERN.matcher(source);
        if (matcher.find()){
            result[0] = matcher.group(1);
            result[1] = matcher.group(2);
        }else{
            matcher = ANY_CHARACTER_PATTERN.matcher(source);

            if(matcher.find()){
                result[0] = matcher.group(0);
            }
        }

        return result;
    }

    /**
     * 将字符串按照逗号分割，示例如下：
     * <table>
     *     <tr>
     *         <th>输入</th>
     *         <th>输出</th>
     *     </tr>
     *
     *     <tr>
     *         <td>reviewer, subscriber</td>
     *         <td>result[0]: reviewer <br/> result[1]: subscriber</td>
     *     </tr>
     *     <tr>
     *         <td>list, edit</td>
     *         <td>result[0]: list <br/> result[1]: edit</td>
     *     </tr>
     *
     * </table>
     *
     * @author zifangsky
     * @date 2019/5/10 14:19
     * @since 1.0.0
     * @param source 原字符串
     * @return java.util.List<java.lang.String>
     */
    public static List<String> splitByComma(String source){
        List<String> result = new LinkedList<>();

        Matcher matcher = SPLIT_BY_COMMA_PATTERN.matcher(source);

        while (matcher.find()){
            result.add(matcher.group());
        }

        return result;
    }

}
