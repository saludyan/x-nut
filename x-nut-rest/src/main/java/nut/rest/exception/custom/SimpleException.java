package nut.rest.exception.custom;

import nut.rest.exception.XRestException;
import nut.thas.defind.rest.RestResponse;
import org.springframework.web.HttpRequestMethodNotSupportedException;

public abstract class SimpleException<T extends Exception>  implements IException{

    protected abstract Class<T> getCustomException();

    @Override
    public RestResponse process(Exception e) {
        Throwable node = e;
        Class<T> custom = this.getCustomException();
        boolean found = false;
        do{
            if(node.getClass() == custom ){
                found = true;
            }else{
                node = node.getCause();
                if(node==null){
                    break;
                }
            }

        }while (!found);
        if(found){
            return RestResponse.failed(XRestException.E4001,node.getMessage());
        }else{
            return null;
        }
    }
}
