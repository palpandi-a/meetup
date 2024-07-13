package com.service;

import com.entities.Authorization;
import com.entities.User;
import com.exception.ValidationException;
import com.persistence.user.UserPersistenceAPI;
import com.persistence.user.UserPersistenceAPIImpl;
import com.util.AuthorizationUtil;
import com.util.TransactionUtil;
import com.validators.UserValidator;

import java.time.LocalDateTime;

public class UserService {

    private final UserPersistenceAPI userPersistenceAPI;

    private final UserValidator validator;

    private final AuthorizationService authorizationService;

    public UserService() {
        this(new UserPersistenceAPIImpl(), new UserValidator(), new AuthorizationService());
    }

    public UserService(UserPersistenceAPI userPersistenceAPI, UserValidator validator, AuthorizationService authorizationService) {
        this.userPersistenceAPI = userPersistenceAPI;
        this.validator = validator;
        this.authorizationService = authorizationService;
    }

    public User create(User user) throws Exception {
        validator.validateUserDetails(user);
        User userDetails = new User.Builder(user)
                .createdTime(LocalDateTime.now())
                .modifiedTime(LocalDateTime.now())
                .password(AuthorizationUtil.hashPassword(user.getPassword()))
                .build();
        User createdUser = TransactionUtil.execute(session -> userPersistenceAPI.save(session, userDetails));
        Authorization authorization = new Authorization.Builder().user(userDetails)
                .authKey(AuthorizationUtil.generateAuthorizationValue(userDetails)).build();
        authorization = authorizationService.add(authorization);
        return new User.Builder(createdUser).authorization(authorization).build();
    }

    public User get(int id) throws Exception {
        User user = TransactionUtil.execute(session -> userPersistenceAPI.get(session, id));
        if (user == null) {
            throw new ValidationException(id + " : invalid");
        }
        return user;
    }

    public User update(User user) throws Exception {
        validator.validateUpdatingUserDetails(user);
        User existingInstance = get(user.getId());
        User updatedInstance = constructUserInstanceWithUpdatedData(existingInstance, user);
        return TransactionUtil.execute(session -> userPersistenceAPI.update(session, updatedInstance));
    }

    public void delete(int userId) throws Exception {
        User user = get(userId);
        TransactionUtil.execute(session -> {
            userPersistenceAPI.delete(session, user);
            return null;
        });
    }

    private User constructUserInstanceWithUpdatedData(User existingObj, User newObj) throws Exception {
        User.Builder builder = new User.Builder(existingObj);
        if (newObj.getName() != null) {
            builder.name(newObj.getName());
        }
        if (newObj.getEmail() != null) {
            builder.email(newObj.getEmail());
        }
        if (newObj.getPassword() != null) {
            String hashedPassword = AuthorizationUtil.hashPassword(newObj.getPassword());
            builder.password(hashedPassword);
        }
        builder.modifiedTime(LocalDateTime.now());
        return builder.build();
    }

    public User getUserByEmailId(String email) throws Exception {
        return TransactionUtil.execute(session -> userPersistenceAPI.getUserDetailsByEmail(session, email));
    }

}
