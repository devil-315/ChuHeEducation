package com.education.base.exception;

/**
 * ClassName：ChuHeEducationException
 *
 * @author: Devil
 * @Date: 2025/1/12
 * @Description:
 * @version: 1.0
 */
public class ChuHeEducationException extends RuntimeException{
    private String errMessage;

    public ChuHeEducationException() {
    }

    public String getErrMessage() {
        return errMessage;
    }

    public ChuHeEducationException(String message) {
        super(message);
        this.errMessage = message;
    }

    public static void cast(String message){
        throw new ChuHeEducationException(message);
    }

    public static void cast(CommonError commonError){
        throw new ChuHeEducationException(commonError.getErrMessage());
    }
}
