package com.seekweb4.chat.api.error;

public class BizException extends RuntimeException{
    public BizException(String message) {
        super(message);
    }
}
