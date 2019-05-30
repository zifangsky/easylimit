package cn.zifangsky.easylimit.enums;

import cn.zifangsky.easylimit.utils.EncryptUtils;

/**
 * 加密方式
 *
 * @author zifangsky
 * @date 2017/11/2
 * @since 1.0.0
 */
public enum EncryptionTypeEnums {
    /**
     * 不加密
     */
    None("None", "No Encryption"),
    /**
     * 自定义加密
     */
    CUSTOM("Custom", "Custom Encryption"),
    /**
     * Base64加密
     */
    Base64("Base64", "Base64 Encryption"),
    /**
     * Md5Hex加密
     */
    Md5Hex("Md5Hex", "Md5Hex Encryption"),
    /**
     * Sha1Hex加密
     */
    Sha1Hex("Sha1Hex", "Sha1Hex Encryption"),
    /**
     * Sha256Hex加密
     */
    Sha256Hex("Sha256Hex", "Sha256Hex Encryption"),
    /**
     * Sha512Hex加密
     */
    Sha512Hex("Sha512Hex", "Sha512Hex Encryption"),
    /**
     * Md5Crypt加密
     */
    Md5Crypt("Md5Crypt", "Md5Crypt Encryption"),
    /**
     * Sha256Crypt加密
     */
    Sha256Crypt("Sha256Crypt", "Sha256Crypt Encryption"),
    /**
     * Sha512Crypt加密
     */
    Sha512Crypt("Sha512Crypt", "Sha512Crypt Encryption");

    /**
     * CODE
     */
    private String code;
    /**
     * 描述
     */
    private String description;

    EncryptionTypeEnums(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 校验密码是否匹配
     *
     * @param correctPassword   数据库中正确的密码
     * @param validatedPassword 来至外部待验证的密码
     * @return boolean
     * @author zifangsky
     * @date 2019/4/3 14:20
     * @since 1.0.0
     */
    public static boolean verifyCredentials(EncryptionTypeEnums type, String correctPassword, String validatedPassword) {
        if (correctPassword == null || validatedPassword == null) {
            throw new IllegalArgumentException("Parameter correctPassword and validatedPassword cannot be empty.");
        }

        switch (type) {
            case None:
                return correctPassword.equals(validatedPassword);
            case Base64:
                return correctPassword.equals(EncryptUtils.base64Encode(validatedPassword));
            case Md5Hex:
                return correctPassword.equals(EncryptUtils.md5Hex(validatedPassword));
            case Sha1Hex:
                return correctPassword.equals(EncryptUtils.sha1Hex(validatedPassword));
            case Sha256Hex:
                return correctPassword.equals(EncryptUtils.sha256Hex(validatedPassword));
            case Sha512Hex:
                return correctPassword.equals(EncryptUtils.sha512Hex(validatedPassword));
            case Md5Crypt:
                return EncryptUtils.checkMd5Crypt(validatedPassword, correctPassword);
            case Sha256Crypt:
                return EncryptUtils.checkSha256Crypt(validatedPassword, correctPassword);
            case Sha512Crypt:
                return EncryptUtils.checkSha512Crypt(validatedPassword, correctPassword);
            default:
                return false;
        }
    }

    public String getCode() {
        return code;
    }

}
