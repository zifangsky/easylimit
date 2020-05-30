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
     * @param length 生成的字符串长度
     */
    public static String getRandomStr(int length) {
        if(length < 1){
            return "";
        }

        char[] chars = new char[length];
        Random rnd = new Random();

        //1. 分别使用随机的大写字母、小写字母、数字填充数组中的几个位置
        chars[nextIndex(chars, rnd)] = nextUpperLetter(rnd);
        if(length >= 2){
            chars[nextIndex(chars, rnd)] = nextLowerLetter(rnd);
        }
        if(length >= 3){
            chars[nextIndex(chars, rnd)] = nextNumLetter(rnd);
        }

        //2. 然后继续填充其他位置的字符
        for (int i = 0; i < length; i++) {
            if (chars[i] == 0) {
                chars[i] = nextChar(rnd);
            }
        }

        //3. 返回结果
        return new String(chars);
    }

    /**
     * 返回一个随机的字符
     */
    private static char nextChar(Random rnd) {
        switch (rnd.nextInt(3)) {
            case 0:
                return (char) ('a' + rnd.nextInt(26));
            case 1:
                return (char) ('A' + rnd.nextInt(26));
            default:
                return (char) ('0' + rnd.nextInt(10));
        }
    }

    /**
     * 查找一个char数组中还没有填充字符的位置
     */
    private static int nextIndex(char[] chars, Random rnd) {
        int index = rnd.nextInt(chars.length);
        while (chars[index] != 0) {
            index = rnd.nextInt(chars.length);
        }
        return index;
    }

    /**
     * 返回一个随机的大写字母
     */
    private static char nextUpperLetter(Random rnd) {
        return (char) ('A' + rnd.nextInt(26));
    }

    /**
     * 返回一个随机的小写字母
     */
    private static char nextLowerLetter(Random rnd) {
        return (char) ('a' + rnd.nextInt(26));
    }

    /**
     * 返回一个随机的数字
     */
    private static char nextNumLetter(Random rnd) {
        return (char) ('0' + rnd.nextInt(10));
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
