package com.XZYHOrderingFood.back.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.XZYHOrderingFood.back.util.Result;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Component
@Slf4j
public class CustomExceptionHandler {
	@ExceptionHandler(value = Exception.class)
    public ResponseEntity<Result> errorHandler(Exception ex) {
        ex.printStackTrace();
        if(ex instanceof CustomException){
            CustomException customException=(CustomException) ex;
            log.error("errorLog=={}",getStackTrace(ex));
            return new ResponseEntity<>(new Result(customException.getCode(),customException.getMsg()),HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            log.error("errorLog=={}",getStackTrace(ex));
            return new ResponseEntity<>(new Result("500","发生异常，请联系客服"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	/**
     * 完整的堆栈信息
     *
     * @param e Exception
     * @return Full StackTrace
     */
    private  static String getStackTrace(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }
}
