package com.linghua.plugin.schedule.exception;

/**
 * Plugin Business Exception
 * 插件业务异常类
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
public class PluginBusinessException extends RuntimeException {
    
    private String errorCode;
    private Object[] args;
    
    public PluginBusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
    }
    
    public PluginBusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public PluginBusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
    }
    
    public PluginBusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public Object[] getArgs() {
        return args;
    }
    
    public void setArgs(Object[] args) {
        this.args = args;
    }
}
