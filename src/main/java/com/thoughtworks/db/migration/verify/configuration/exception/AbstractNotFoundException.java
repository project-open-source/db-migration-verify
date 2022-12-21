package com.thoughtworks.db.migration.verify.configuration.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AbstractNotFoundException extends  RuntimeException{
    public static final String NOT_FOUND = "NOT_FOUND";
    private final String logMessage;

    protected AbstractNotFoundException(String errorMessage, String logMessage) {
        super(errorMessage);
        this.logMessage = logMessage;
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    public String getBusinessCode(){
        return NOT_FOUND;
    }
}
