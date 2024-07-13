package com.validators;

import com.entities.User;
import com.exception.ValidationException;
import com.persistence.user.UserPersistenceAPI;
import com.persistence.user.UserPersistenceAPIImpl;
import com.util.TransactionUtil;

import java.util.regex.Pattern;

public class UserValidator {

    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    private final UserPersistenceAPI persistenceAPI;

    public UserValidator() {
        persistenceAPI = new UserPersistenceAPIImpl();
    }

    public void validateUpdatingUserDetails(User user) throws Exception {
        if (user.getEmail() != null) {
            validateEmailDetails(user.getEmail());
        }
    }

    public void validateUserDetails(User user) throws Exception {
        if (isNullOrEmpty(user.getName())) {
            throw new ValidationException("'name' is a mandatory field");
        }
        if (isNullOrEmpty(user.getPassword())) {
            throw new ValidationException("'password' is a mandatory field");
        }
        if (isNullOrEmpty(user.getEmail())) {
            throw new ValidationException("'email' is a mandatory field");
        }
        validateEmailDetails(user.getEmail());
    }

    private void validateEmailDetails(String email) throws Exception {
        if (!isValidEmail(email)) {
            throw new ValidationException("invalid email address");
        }
        if (isEmailAlreadyExist(email)) {
            throw new ValidationException("The given email is already in use");
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    private boolean isEmailAlreadyExist(String email) throws Exception {
        return TransactionUtil.execute(session -> persistenceAPI.getUserDetailsByEmail(session, email)) != null;
    }

}
