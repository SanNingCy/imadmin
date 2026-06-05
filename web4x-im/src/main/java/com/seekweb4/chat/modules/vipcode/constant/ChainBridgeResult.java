package com.seekweb4.chat.modules.vipcode.constant;

/**
 * 链桥调用结果（参考 WXTransactionResult，支持携带 data）
 */
public class ChainBridgeResult<T> {

    private final boolean success;
    private final String errorMessage;
    private final T data;

    private ChainBridgeResult(boolean success, String errorMessage, T data) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public static <T> ChainBridgeResult<T> success(T data) {
        return new ChainBridgeResult<>(true, null, data);
    }

    public static <T> ChainBridgeResult<T> failure(String errorMessage) {
        return new ChainBridgeResult<>(false, errorMessage, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public T getData() {
        return data;
    }
}
