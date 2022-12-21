package com.thoughtworks.db.migration.verify.configuration.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AbstractBusinessException extends RuntimeException {
    private final String logMessage;

    protected AbstractBusinessException(String errorMessage, String logMessage) {
        super(errorMessage);
        this.logMessage = logMessage;
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    public String getBusinessCode() {
        return "BUSINESS_EXCEPTION";
    }
}