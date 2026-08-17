package org.ledgerark.common.entity;


import lombok.Data;
import org.ledgerark.common.enums.ResultCode;

@Data
public class Result<T> {

    private String code;
    private String msg;
    private T data;

    public static <T> Result<T> success(){
        Result<T> result = new Result<T>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        return result;
    }

    public static <T> Result<T> success(T data){
        Result<T> result = success();
        result.setData(data);
        return result;
    }

    public static <T> Result<T> failure(){
        return failure(ResultCode.SYSTEM_ERROR);
    }

    public static <T> Result<T> failure(ResultCode resultCode){
        Result<T> result = new Result<T>();
        result.setCode(resultCode.getCode());
        result.setMsg(resultCode.getMsg());
        return result;
    }

    public static <T> Result<T> failure(ResultCode resultCode, T data){
        Result<T> result = failure(resultCode);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> failure(String code, String message){
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMsg(message);
        return result;
    }

    public static <T> Result<T> failure(String code, String message, T data) {
        Result<T> result = failure(code, message);
        result.setData(data);
        return result;
    }
}
