package cn.zifangsky.easylimit.session.impl.support;

import cn.zifangsky.easylimit.session.SessionIdFactory;
import cn.zifangsky.easylimit.utils.StringUtils;

import java.io.Serializable;

/**
 * 通过随机字符串生成sessionId
 *
 * @author zifangsky
 * @date 2019/4/2
 * @since 1.0.0
 */
public class RandomCharacterSessionIdFactory implements SessionIdFactory {
    /**
     * 默认的生成的字符串长度
     */
    private static final int DEFAULT_STRLEN = 30;
    /**
     * 生成的字符串长度
     */
    private int strLen;

    public RandomCharacterSessionIdFactory() {
        this.strLen = DEFAULT_STRLEN;
    }

    public RandomCharacterSessionIdFactory(int strLen) {
        if (strLen < 1) {
            throw new IllegalArgumentException("strLen cannot be less than 1");
        }

        this.strLen = strLen;
    }

    @Override
    public Serializable generateSessionId() {
        return StringUtils.getRandomStr(this.strLen);
    }

    public int getStrLen() {
        return strLen;
    }
}
