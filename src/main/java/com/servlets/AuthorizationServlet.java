package com.servlets;

import com.entities.Authorization;
import com.entities.User;
import com.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.io.wrapper.CustomResponseWrapper;
import com.service.AuthorizationService;
import com.service.UserService;
import com.util.AuthorizationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class AuthorizationServlet extends HttpServlet {

    private UserService userService;

    private AuthorizationService authorizationService;

    private ObjectMapper mapper;

    private RequestProcessor requestProcessor;

    @Override
    public void init() throws ServletException {
        userService = new UserService();
        mapper = new ObjectMapper();
        requestProcessor = new RequestProcessor();
        authorizationService = new AuthorizationService();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        String path = uri.substring(uri.lastIndexOf('/'));
        switch (path) {
            case "/signup":
                requestProcessor.processRequest(request, response, this::handleSignupRequest);
                break;
            case "/login":
                requestProcessor.processRequest(request, response, this::handleLoginRequest);
                break;
            case "/logout":
                requestProcessor.processRequest(request, response, this::handleLogoutRequest);
                break;
            default:
                new CustomResponseWrapper(response).returnBadRequestResponse("Invalid POST request");
        }
    }

    private void handleSignupRequest(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        User user = mapper.readValue(request.getInputStream(), User.Builder.class).build();
        user = userService.create(user);
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        responseWrapper.addHeaders("Authorization", user.getAuthorization().getAuthKey());
        responseWrapper.returnCreatedResponse(mapper.writeValueAsString(user));
    }

    private void handleLoginRequest(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        JSONObject credential = new JSONObject(new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
        validateCredential(credential);
        User user = userService.getUserByEmailId(credential.get("email").toString());
        if (user == null) {
            responseWrapper.returnUnProcessableEntityErrorResponse("Invalid email: " + credential.get("email").toString());
            return;
        }
        String hashedPassword = AuthorizationUtil.hashPassword(credential.get("password").toString());
        if (!Objects.equals(user.getPassword(), hashedPassword)) {
            responseWrapper.returnUnAuthorized();
            return;
        }
        HttpSession session = request.getSession();
        User userSession = (User) session.getAttribute("user");
        if (session.getAttribute("user") != null) {
            if (userSession.getEmail().equals(credential.get("email").toString())) {
                responseWrapper.addHeaders("Authorization", user.getAuthorization().getAuthKey());
                responseWrapper.returnOkResponse(new JSONObject().put("message", "You already logged in").toString());
            } else {
                responseWrapper.returnBadRequestResponse("You can't perform this operation");
            }
            return;
        }

        if (user.getAuthorization() == null) {
            Authorization authorization = new Authorization.Builder().user(user)
                    .authKey(AuthorizationUtil.generateAuthorizationValue(user)).build();
            authorization = authorizationService.add(authorization);
            user = new User.Builder(user).authorization(authorization).build();
        }

        session.setAttribute("user", user);
        responseWrapper.addHeaders("Authorization", user.getAuthorization().getAuthKey());
        responseWrapper.returnOkResponse(mapper.writeValueAsString(user));
    }

    private void validateCredential(JSONObject credential) throws ValidationException {
        if (!credential.has("email")) {
            throw new ValidationException("'email' is a mandatory field");
        }
        if (credential.get("email").toString().isEmpty()) {
            throw new ValidationException("Invalid email: " + credential.get("email").toString());
        }
        if (!credential.has("password")) {
            throw new ValidationException("'password' is a mandatory field");
        }
        if (credential.get("password").toString().isEmpty()) {
            throw new ValidationException("Invalid password: " + credential.get("password").toString());
        }
    }

    private void handleLogoutRequest(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        authorizationService.delete(user.getAuthorization());
        session.invalidate();
        responseWrapper.returnNoContentResponse();
    }

}
