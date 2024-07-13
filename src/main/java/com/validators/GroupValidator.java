package com.validators;

import com.entities.Group;
import com.exception.ValidationException;

public class GroupValidator {

    public void validateGroupDetails(Group group) throws ValidationException {
        if (isNullOrEmpty(group.getName())) {
            throw new ValidationException("'name' is a mandatory field");
        }
        if (group.getCreatedBy() == null) {
            throw new ValidationException("'createdBy' is a mandatory field");
        }
    }

    public boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

}
