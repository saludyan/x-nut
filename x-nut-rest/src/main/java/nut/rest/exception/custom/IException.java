package nut.rest.exception.custom;

import nut.thas.defind.rest.RestResponse;

public interface IException {

    default RestResponse process(Exception e){
        return null;
    }
}
