package com.seekweb4.chat.modules.vipcode.constant;

/**
 * 链桥调用业务异常（参考 WXTransactionException）
 */
public class ChainBridgeException extends Exception {

    public ChainBridgeException(String message) {
        super(message);
    }
}
