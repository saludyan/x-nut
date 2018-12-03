package nut.thas.defind.rest;

import lombok.Data;
import nut.thas.defind.exceptions.GlobalException;

/**
 * Created by Super Yan on 2018/11/29.
 */
@Data
public class RestResponse<T> {

    //是否请求成功
    private boolean success;

    //如果success = false ，必须提供错误代号
    private String errorCode;

    //如果success = false,指出错误消息
    private String errorMsg;

    private String errorDetail;

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
        this.data=t;
    }

    public RestResponse(boolean result,String errorMsg){
        this.success = result;
        if(!result){
            this.errorMsg = errorMsg;
        }
    }

    public RestResponse(boolean success, String errorCode, String errorMsg, String errorDetail, String errorStacks, T data) {
        this.success = success;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.errorDetail = errorDetail;
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

    /**
     * 提供失败对象的静态方法，方便输出
     * @param errorCode  错误码
     * @param errorMsg   错误消息
     * @return
     */
    public static RestResponse failed(String errorCode,String errorMsg){
        return failed(errorCode, errorMsg, errorMsg);
    }

    public static RestResponse failed(String errorMsg){
        RestResponse restResponse = RestResponse.failed("10000",errorMsg);
        return restResponse;
    }

    public static RestResponse failed(String errorCode,String errorMsg,String errorDetail){
        RestResponse restResponse = new RestResponse();
        restResponse.setSuccess(false);
        restResponse.setErrorCode(errorCode);
        restResponse.setErrorMsg(errorMsg);
        restResponse.setErrorDetail(errorDetail);
        return restResponse;
    }

    public static RestResponse failed(String errorCode,String errorMsg,String errorDetail,String errorStacks){
        RestResponse restResponse = new RestResponse();
        restResponse.setSuccess(false);
        restResponse.setErrorCode(errorCode);
        restResponse.setErrorMsg(errorMsg);
        restResponse.setErrorDetail(errorDetail);
        restResponse.setErrorStacks(errorStacks);
        return restResponse;
    }

    public static RestResponse failed(GlobalException enumExceptionCode){
        RestResponse restResponse = new RestResponse();
        restResponse.setSuccess(false);
        restResponse.setErrorMsg(enumExceptionCode.getEnglish());
        restResponse.setErrorDetail(enumExceptionCode.getChinese());
        restResponse.setErrorCode(enumExceptionCode.getCode());
        return restResponse;
    }


    public static RestResponse failed(GlobalException enumExceptionCode, String customError){
        RestResponse restResponse = new RestResponse();
        restResponse.setSuccess(false);
        restResponse.setErrorMsg(enumExceptionCode.getErrorMsg());
        restResponse.setErrorDetail(customError);
        restResponse.setErrorCode(enumExceptionCode.getCode());
        return restResponse;
    }
}
