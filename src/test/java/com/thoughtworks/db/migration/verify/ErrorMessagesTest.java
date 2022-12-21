package com.thoughtworks.db.migration.verify;

import com.thoughtworks.db.migration.verify.model.ErrorMessages;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ErrorMessagesTest {
    @Test
    void should_success_create_error_message() {
        var errorMessages = new ErrorMessages();

        assertTrue(Objects.nonNull(errorMessages.getValues()));
        assertFalse(errorMessages.isNotEmpty());
    }

    @Test
    void should_success_add_error_message_info_values() {
        var errorMessages = new ErrorMessages();
        errorMessages.add("error");

        assertEquals(1, errorMessages.getValues().size());
        assertTrue(errorMessages.isNotEmpty());
        assertEquals("error", errorMessages.getValues().get(0));
    }
}