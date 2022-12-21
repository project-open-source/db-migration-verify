package com.thoughtworks.db.migration.verify.model;

import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ErrorMessages {
    private final List<String> values;

    public ErrorMessages() {
        this.values = new ArrayList<>();
    }

    public boolean isNotEmpty() {
        return CollectionUtils.isNotEmpty(values);
    }

    public void add(String errorMessage) {
        this.values.add(errorMessage);
    }

    public void addAll(ErrorMessages errorMessages) {
        for (String errorMessage : errorMessages.values) {
            add(errorMessage);
        }
    }
}
