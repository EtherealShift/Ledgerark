package org.ledgerark.system.constant;

public class UserConstant {

    /**
     * 用户名长度限制
     */
    public static final int USERNAME_MIN_LENGTH = 2;
    public static final int USERNAME_MAX_LENGTH = 20;

    /**
     * 密码长度限制
     */
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int PASSWORD_MAX_LENGTH = 20;


    /**
     * Session中用户信息的key
     */
    public static final String SESSION_USER_KEY = "loginUser";


}
