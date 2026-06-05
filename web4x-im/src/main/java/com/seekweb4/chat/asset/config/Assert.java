package com.seekweb4.chat.asset.config;

/**
 * @author coderpwh
 */
import com.seekweb4.chat.common.json.AjaxJson;
import org.springframework.util.StringUtils;

public class Assert {

    /**
     * 断言对象不为空，为空则抛出异常
     */
    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言对象不为空，为空则抛出异常
     */
    public static void notNull(Object obj, AjaxJson resultCode) {
        if (obj == null) {
            throw new BusinessException(resultCode);
        }
    }

    /**
     * 断言对象为空，不为空则抛出异常
     */
    public static void isNull(Object obj, String message) {
        if (obj != null) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言对象为空，不为空则抛出异常
     */
    public static void isNull(Object obj, AjaxJson resultCode) {
        if (obj != null) {
            throw new BusinessException(resultCode);
        }
    }

    /**
     * 断言字符串不为空，为空则抛出异常
     */
    public static void hasText(String text, String message) {
        if (!StringUtils.hasText(text)) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言字符串不为空，为空则抛出异常
     */
    public static void hasText(String text, AjaxJson resultCode) {
        if (!StringUtils.hasText(text)) {
            throw new BusinessException(resultCode);
        }
    }

    /**
     * 断言条件为真，为假则抛出异常
     */
    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言条件为真，为假则抛出异常
     */
    public static void isTrue(boolean condition, AjaxJson resultCode) {
        if (!condition) {
            throw new BusinessException(resultCode);
        }
    }

    /**
     * 断言条件为假，为真则抛出异常
     */
    public static void isFalse(boolean condition, String message) {
        if (condition) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言条件为假，为真则抛出异常
     */
    public static void isFalse(boolean condition, AjaxJson resultCode) {
        if (condition) {
            throw new BusinessException(resultCode);
        }
    }
}
