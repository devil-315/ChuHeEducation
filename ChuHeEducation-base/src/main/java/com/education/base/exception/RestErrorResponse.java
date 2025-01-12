package com.education.base.exception;

import java.io.Serializable;

/**
 * ClassName：RestErrorResponse
 *
 * @author: Devil
 * @Date: 2025/1/12
 * @Description:
 * @version: 1.0
 */
public class RestErrorResponse implements Serializable {
    private String errMessage;

    public RestErrorResponse(String errMessage){
        this.errMessage= errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
