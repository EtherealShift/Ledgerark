package org.ledgerark.common.common;


import lombok.Data;

@Data
public class Result<T> {

    private String code;
    private String msg;
    private T data;

    public static <T> Result<T> success(){
        Result<T> result = new Result<T>();
        result.setCode("200");
        result.setMsg("SUCCESS");
        return result;
    }

    public static <T> Result<T> success(T data){
        Result<T> result = success();
        result.setData(data);
        return result;
    }

    public static <T> Result<T> failure(){
        Result<T> result = new Result<T>();
        result.setCode("500");
        result.setMsg("ERROR");
        return result;
    }

    public static <T> Result<T> failure(String code, String msg){
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> failure(String code, String msg, T data){
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

}
