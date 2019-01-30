package nut.thas.defind.rest;

import lombok.Data;
import lombok.experimental.Accessors;
import nut.thas.exceptions.GlobalException;

/**
 * Created by Super Yan on 2018/11/29.
 */
@Data
@Accessors(chain = true)
public class RestResponse<T> {

    //是否请求成功
    private boolean success;

    //如果success = false ，必须提供错误代号
    private String errorCode;

    //如果success = false,指出错误消息
    private String errorMsg;

    // 错误堆栈
    private String errorStacks;

    //如果success = true,可提供返回对象，也可以不提供
    private T data;



    public RestResponse(){}
    public RestResponse(boolean result){
        this.success = result;
    }

    public RestResponse(boolean result,T t){
        this.success = result;
        this.data=t;
    }

    public RestResponse(boolean result,String errorMsg,T t){
        this.success = result;
        this.errorMsg = errorMsg;
        this.errorCode ="4001";
        this.data=t;
    }

    public RestResponse(boolean result,String errorMsg){
        this.success = result;
        if(!result){
            this.errorMsg = errorMsg;
        }
    }

    public RestResponse(boolean success, String errorCode, String errorMsg, String errorStacks, T data) {
        this.success = success;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.errorStacks = errorStacks;
        this.data = data;
    }

    /**
     * 提供成功的静态方法，方便使用
     * @param data 希望作为返回值输出的结果对象
     * @return
     *                  返回success为true的相应对象
     */
    public static <T> RestResponse<T> success(T data){
        RestResponse restResponse = new RestResponse();
        restResponse.setSuccess(true);
        restResponse.setData(data);
        return restResponse;
    }


    public static RestResponse failed(String errorCode,String errorMsg){
        return new RestResponse()
                .setErrorCode(errorCode)
                .setErrorMsg(errorMsg)
                .setSuccess(false)
                ;
    }

    public static RestResponse failed(String errorCode,String errorMsg,String errorStacks){
        return new RestResponse()
                .setErrorCode(errorCode)
                .setErrorMsg(errorMsg)
                .setSuccess(false)
                .setErrorStacks(errorStacks)
                ;
    }

    public static RestResponse failed(String errorMsg){
        RestResponse restResponse = RestResponse.failed("4001",errorMsg);
        return restResponse;
    }




    public static RestResponse failed(GlobalException enumExceptionCode){
        RestResponse restResponse = new RestResponse();
        restResponse.setSuccess(false);
        restResponse.setErrorMsg(enumExceptionCode.getEnglish());
        restResponse.setErrorCode(enumExceptionCode.getCode());
        return restResponse;
    }


    public static RestResponse failed(GlobalException enumExceptionCode, String customError){
        RestResponse restResponse = new RestResponse();
        restResponse.setSuccess(false);
        restResponse.setErrorMsg(customError);
        restResponse.setErrorCode(enumExceptionCode.getCode());
        return restResponse;
    }
}
