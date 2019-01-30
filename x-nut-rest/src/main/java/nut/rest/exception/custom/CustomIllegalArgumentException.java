package nut.rest.exception.custom;

import nut.thas.defind.rest.RestResponse;

public class CustomIllegalArgumentException extends SimpleException<IllegalArgumentException> implements IException{


    @Override
    protected Class<IllegalArgumentException> getCustomException() {
        return IllegalArgumentException.class;
    }
}
