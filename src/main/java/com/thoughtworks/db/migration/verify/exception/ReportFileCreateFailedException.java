package com.thoughtworks.db.migration.verify.exception;

import com.thoughtworks.db.migration.verify.configuration.exception.AbstractBusinessException;

public class ReportFileCreateFailedException extends AbstractBusinessException {
    public ReportFileCreateFailedException(String errorMessage, String logMessage) {
        super(errorMessage, logMessage);
    }
}
