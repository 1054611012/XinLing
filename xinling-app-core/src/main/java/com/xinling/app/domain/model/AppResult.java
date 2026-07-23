package com.xinling.app.domain.model;

/**
 * APP通用响应结果
 */
public class AppResult<T> {

    private int code;
    private String message;
    private T data;
    private long timestamp;

    public AppResult() {
        this.timestamp = System.currentTimeMillis();
    }

    public AppResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> AppResult<T> success(T data) {
        return new AppResult<>(200, "success", data);
    }

    public static AppResult<Void> success() {
        return new AppResult<>(200, "success", null);
    }

    public static AppResult<Void> successMsg(String message) {
        return new AppResult<>(200, message, null);
    }

    public static <T> AppResult<T> success(String message, T data) {
        return new AppResult<>(200, message, data);
    }

    public static <T> AppResult<T> error(int code, String message) {
        return new AppResult<>(code, message, null);
    }

    public static <T> AppResult<T> error(String message) {
        return new AppResult<>(400, message, null);
    }

    public static <T> AppResult<T> unauthorized(String message) {
        return new AppResult<>(401, message, null);
    }

    public static <T> AppResult<T> forbidden(String message) {
        return new AppResult<>(403, message, null);
    }

    // --- getters/setters ---

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
