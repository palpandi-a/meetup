package com.util;

import com.entities.User;

import java.util.Base64;

public class AuthorizationUtil {

    public static String hashPassword(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    public static String generateAuthorizationValue(User user) {
        String saltText = user.getEmail() + "-" + user.getPassword() + "-" + System.currentTimeMillis();
        return AuthorizationUtil.hashPassword(saltText);
    }

}
