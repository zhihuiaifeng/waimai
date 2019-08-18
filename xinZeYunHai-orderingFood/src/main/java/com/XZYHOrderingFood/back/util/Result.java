package com.XZYHOrderingFood.back.util;
 

import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
 

 

/**
 * 状态返回
 */
@SuppressWarnings("deprecation")
@Data
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Result<T> implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 5554154852353507801L;

	private String code;

    private String msg;
     
    private T data;

    public Result() {
    }


    public Result(String code, String msg) {
        this(code,msg,null);
    }

    public Result(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    
    

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
    
    public static <T> Result<T> resultData(String code, String msg, T data){
    	return new Result<T>(code,msg,data) ;
    }
}
