package com.linghua.plugin.schedule.exception;

/**
 * Plugin Validation Exception
 * 插件验证异常类
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
public class PluginValidationException extends RuntimeException {
    
    private String errorCode;
    private Object[] args;
    
    public PluginValidationException(String message) {
        super(message);
        this.errorCode = "VALIDATION_ERROR";
    }
    
    public PluginValidationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public PluginValidationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "VALIDATION_ERROR";
    }
    
    public PluginValidationException(String errorCode, String message, Throwable cause) {
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
