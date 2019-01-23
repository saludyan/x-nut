package nut.rest;

/**
 * @author hone 2018/4/26
 * @since
 */
public interface XConverter<T> {

    boolean supports(Object o);

    String convert(T t);
}
