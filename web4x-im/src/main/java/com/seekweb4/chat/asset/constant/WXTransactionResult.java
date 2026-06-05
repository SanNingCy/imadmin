package com.seekweb4.chat.asset.constant;

/**
 * @author coderpwh
 */
public class WXTransactionResult {

    public final boolean success;
    public final String errorMessage;

    public WXTransactionResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static WXTransactionResult success() {
        return new WXTransactionResult(true, null);
    }

    public static WXTransactionResult failure(String errorMessage) {
        return new WXTransactionResult(false, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
