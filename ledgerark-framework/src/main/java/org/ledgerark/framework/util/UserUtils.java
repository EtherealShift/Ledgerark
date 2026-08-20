package org.ledgerark.framework.util;


import cn.dev33.satoken.stp.StpUtil;
import org.ledgerark.common.constant.UserConstant;
import org.ledgerark.common.entity.LoginUser;

public class UserUtils {



    /**
     * 获取当前登录用户
     */
    public static LoginUser getCurrentUser() {
        return (LoginUser) StpUtil.getSession().get(UserConstant.SESSION_USER_KEY);
    }


    /**
     * 获取当前用户名称
     */
    public static String getCurrentUserName() {
        return getCurrentUser().getUsername();
    }

    /**
     * 获取用户当前用户ID
     */
    public static Long getCurrentUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    /**
     * 获取当前用户邮箱
     */
    public static String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

    /**
     * 获取当前用户昵称
     */
    public static String getCurrentUserNickname() {
        return getCurrentUser().getNickname();
    }

    /**
     * 获取当前用户类型
     */
    public static String getCurrentUserType() {
        return getCurrentUser().getUserType();
    }



}
