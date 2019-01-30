package nut.thas.exceptions;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Collection;
import java.util.Map;

public class XAssert {

    public XAssert() {
    }

    public static void isTrue(boolean expression, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        if (!expression) {
            throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
        }
    }

    public static void isTrue(boolean expression) throws IllegalArgumentException {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    public static void isTrue(boolean expression, GlobalException globalException) throws XException {
        if (!expression) {
            throw new XException(globalException);
        }
    }

    public static void isFalse(boolean expression, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        if (expression) {
            throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
        }
    }

    public static void isFalse(boolean expression) throws IllegalArgumentException {
        isFalse(expression, "[Assertion failed] - this expression must be false");
    }

    public static void isFalse(boolean expression,GlobalException globalException) throws XException {
        if (expression) {
            throw new XException(globalException);
        }
    }

    public static void isNull(Object object, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        if (object != null) {
            throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
        }
    }

    public static void isNull(Object object) throws NullPointerException {
        isNull(object, "[Assertion failed] - the object argument must be null");
    }

    public static void isNull(Object object, GlobalException globalException) throws XException {
        if (object != null) {
            throw new XException(globalException);
        }
    }

    public static <T> T notNull(T object, String errorMsgTemplate, Object... params) throws NullPointerException {
        if (object == null) {
            throw new NullPointerException(StrUtil.format(errorMsgTemplate, params));
        } else {
            return object;
        }
    }

    public static <T> T notNull(T object, GlobalException globalException) throws XException {
        if (object == null) {
            throw new XException(globalException);
        } else {
            return object;
        }
    }

    public static <T> T notNull(T object) throws NullPointerException {
        return notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    public static String notEmpty(String text, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        if (StrUtil.isEmpty(text)) {
            throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
        } else {
            return text;
        }
    }

    public static String notEmpty(String text, GlobalException globalException) throws XException {
        if (StrUtil.isEmpty(text)) {
            throw new XException(globalException);
        } else {
            return text;
        }
    }

    public static String notEmpty(String text) throws IllegalArgumentException {
        return notEmpty(text, "[Assertion failed] - this String argument must have length; it must not be null or empty");
    }

    public static String notBlank(String text, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        if (StrUtil.isBlank(text)) {
            throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
        } else {
            return text;
        }
    }

    public static String notBlank(String text, GlobalException globalException) throws XException {
        if (StrUtil.isBlank(text)) {
            throw new XException(globalException);
        } else {
            return text;
        }
    }

    public static String notBlank(String text) throws IllegalArgumentException {
        return notBlank(text, "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
    }

    public static String notContain(String textToSearch, String substring, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        if (StrUtil.isNotEmpty(textToSearch) && StrUtil.isNotEmpty(substring) && textToSearch.contains(substring)) {
            throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
        } else {
            return substring;
        }
    }

    public static String notContain(String textToSearch, String substring, GlobalException globalException) throws XException {
        if (StrUtil.isNotEmpty(textToSearch) && StrUtil.isNotEmpty(substring) && textToSearch.contains(substring)) {
            throw new XException(globalException);
        } else {
            return substring;
        }
    }

    public static String notContain(String textToSearch, String substring) throws IllegalArgumentException {
        return notContain(textToSearch, substring, "[Assertion failed] - this String argument must not contain the substring [{}]", substring);
    }

    public static Object[] notEmpty(Object[] array, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        if (ArrayUtil.isEmpty(array)) {
            throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
        } else {
            return array;
        }
    }

    public static Object[] notEmpty(Object[] array, GlobalException globalException) throws XException {
        if (ArrayUtil.isEmpty(array)) {
            throw new XException(globalException);
        } else {
            return array;
        }
    }

    public static Object[] notEmpty(Object[] array) throws IllegalArgumentException {
        return notEmpty(array, "[Assertion failed] - this array must not be empty: it must contain at least 1 element");
    }

    public static <T> T[] noNullElements(T[] array, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        if (ArrayUtil.hasNull(array)) {
            throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
        } else {
            return array;
        }
    }

    public static <T> T[] noNullElements(T[] array, GlobalException globalException) throws XException {
        if (ArrayUtil.hasNull(array)) {
            throw new XException(globalException);
        } else {
            return array;
        }
    }

    public static <T> T[] noNullElements(T[] array) throws IllegalArgumentException {
        return noNullElements(array, "[Assertion failed] - this array must not contain any null elements");
    }

    public static <T> Collection<T> notEmpty(Collection<T> collection, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        if (CollectionUtil.isEmpty(collection)) {
            throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
        } else {
            return collection;
        }
    }

    public static <T> Collection<T> notEmpty(Collection<T> collection, GlobalException globalException) throws XException {
        if (CollectionUtil.isEmpty(collection)) {
            throw new XException(globalException);
        } else {
            return collection;
        }
    }

    public static <T> Collection<T> notEmpty(Collection<T> collection) throws IllegalArgumentException {
        return notEmpty(collection, "[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
    }

    public static <K, V> Map<K, V> notEmpty(Map<K, V> map, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        if (CollectionUtil.isEmpty(map)) {
            throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
        } else {
            return map;
        }
    }

    public static <K, V> Map<K, V> notEmpty(Map<K, V> map) throws IllegalArgumentException {
        return notEmpty(map, "[Assertion failed] - this map must not be empty; it must contain at least one entry");
    }

    public static <T> T isInstanceOf(Class<?> type, T obj) {
        return isInstanceOf(type, obj, "Object [{}] is not instanceof [{}]", obj, type);
    }

    public static <T> T isInstanceOf(Class<?> type, T obj, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
        } else {
            return obj;
        }
    }

    public static <T> T isInstanceOf(Class<?> type, T obj, GlobalException globalException) throws XException {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            throw new XException(globalException);
        } else {
            return obj;
        }
    }

    public static void isAssignable(Class<?> superType, Class<?> subType) throws IllegalArgumentException {
        isAssignable(superType, subType, "{} is not assignable to {})", subType, superType);
    }

    public static void isAssignable(Class<?> superType, Class<?> subType, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        notNull(superType, "Type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
        }
    }

    public static void isAssignable(Class<?> superType, Class<?> subType, GlobalException globalException) throws XException {
        notNull(superType, "Type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new XException(globalException);
        }
    }

    public static void state(boolean expression, String errorMsgTemplate, Object... params) throws IllegalStateException {
        if (!expression) {
            throw new IllegalStateException(StrUtil.format(errorMsgTemplate, params));
        }
    }

    public static void state(boolean expression, GlobalException globalException) throws XException {
        if (!expression) {
            throw new XException(globalException);
        }
    }

    public static void state(boolean expression) throws IllegalStateException {
        state(expression, "[Assertion failed] - this state invariant must be true");
    }
}
