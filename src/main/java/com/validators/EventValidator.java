package com.validators;

import com.entities.Creator;
import com.entities.Event;
import com.exception.ValidationException;

public class EventValidator {

    public void validateEventDetails(Event event) throws ValidationException {
        if (isNullOrEmpty(event.getTitle())) {
            throw new ValidationException("'title' is a mandatory field");
        }
        if (event.getEventTime() == null) {
            throw new ValidationException("'eventTime' is a mandatory field");
        }
    }

    public void validateCreatorDetails(Creator creator) throws ValidationException {
        if (creator == null) {
            throw new ValidationException("'created_by' is invalid");
        }
    }

    public void validateUpdatingEventDetails(Event event) throws ValidationException {
        if (event.getTitle() == null) {
            throw new ValidationException("'title' is a mandatory field");
        }
    }

    public boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

}
