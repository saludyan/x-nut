package nut.rest.exception.custom;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import nut.rest.exception.XRestException;
import nut.thas.defind.rest.RestResponse;

public class CustomMySqlException implements IException {

    @Override
    public RestResponse process(Exception e) {
        Throwable node = e;
        boolean found = false;
        do{
            if(node instanceof MySQLIntegrityConstraintViolationException){
                found = true;
            }else{
                node = node.getCause();
                if(node==null){
                    break;
                }
            }

        }while (!found);
        if(found){
            MySQLIntegrityConstraintViolationException mySqlE = (MySQLIntegrityConstraintViolationException) node;
            return RestResponse.failed(mySqlE.getSQLState(),mySqlE.getMessage(), ExceptionUtil.stacktraceToOneLineString(e,800));
        }else{
            return null;
        }

    }
}
