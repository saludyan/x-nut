package nut.rest.exception.custom;

import nut.rest.exception.XRestException;
import nut.thas.defind.rest.RestResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

public class CustomBadCredentialsException implements IException {

    @Override
    public RestResponse process(Exception e) {
        Throwable node = e;
        boolean found = false;
        do{
            if(node instanceof BadCredentialsException){
                found = true;
            }else{
                node = node.getCause();
                if(node==null){
                    break;
                }
            }

        }while (!found);
        if(found){
            BadCredentialsException requestE = (BadCredentialsException) node;
            return RestResponse.failed(XRestException.E4001,requestE.getMessage());
        }else{
            return null;
        }
    }
}
