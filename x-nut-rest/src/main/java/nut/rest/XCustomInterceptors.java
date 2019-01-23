package nut.rest;


import java.util.List;

/**
 * Created by Super Yan on 2018/5/22.
 */
public interface XCustomInterceptors {

    default List<XInterceptorRegistration> getInterceptors(){
        return null;
    }
}
