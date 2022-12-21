package com.thoughtworks.db.migration.verify.configuration;

import com.thoughtworks.db.migration.verify.configuration.exception.AbstractBusinessException;
import com.thoughtworks.db.migration.verify.configuration.exception.AbstractNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerExceptionHandler {

    public static final String ILLEGAL_ARGUMENT = "ILLEGAL_ARGUMENT";

    @ExceptionHandler(AbstractBusinessException.class)
    public ResponseEntity<ErrorResult> handleBusinessException(AbstractBusinessException e) {
        log.error("handleBusinessException: " + e.getLogMessage(), e);
        return ResponseEntity.status(e.getHttpStatus())
                             .body(new ErrorResult(e.getBusinessCode(), e.getMessage()));
    }

    @ExceptionHandler(AbstractNotFoundException.class)
    public ResponseEntity<ErrorResult> handleNotFoundException(AbstractNotFoundException e) {
        log.error("handleNotFoundException: " + e.getLogMessage(), e);
        return ResponseEntity.status(e.getHttpStatus())
                             .body(new ErrorResult(e.getBusinessCode(), e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResult handleRuntimeException(Exception e) {
        log.error("handleRuntimeException: " + e.getMessage(), e);
        return new ErrorResult("INTERNAL_SERVER_ERROR", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResult handleBadRequestException(IllegalArgumentException e) {
        logBadRequestException(e);
        return new ErrorResult(ILLEGAL_ARGUMENT, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResult handleBadRequestException(MethodArgumentNotValidException e) {
        logBadRequestException(e);
        return new ErrorResult(ILLEGAL_ARGUMENT, Optional.ofNullable(e.getBindingResult().getFieldError())
                                                         .map(FieldError::getDefaultMessage)
                                                         .orElse("参数校验失败"));
    }

    private void logBadRequestException(Exception e) {
        log.error("handleBadRequestException: " + e.getMessage(), e);
    }
}
