package com.io.wrapper;

import com.exception.ValidationException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomResponseWrapper extends HttpServletResponseWrapper {

    private final Map<String, String> headers = new HashMap<>();

    public CustomResponseWrapper(HttpServletResponse response) {
        super(response);
        setContentType("application/json");
    }

    public void addHeaders(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
        System.out.println("headers = " + headers);
    }

    private void applyHeaders(HttpServletResponse response) {
        headers.forEach(response::setHeader);
        headers.clear();
    }

    public void returnBadRequestResponse(String message) throws IOException {
        setErrorResponse(400, message);
    }

    public void returnUnAuthorized() throws IOException {
        returnUnAuthorized("Invalid credentials");
    }

    public void returnUnAuthorized(String message) throws IOException {
        setErrorResponse(401, message);
    }

    public void returnForbiddenErrorResponse(String message) throws IOException {
        setStatus(403);
        getHttpServletResponse().getOutputStream().write(message.getBytes());
    }

    public void returnUnProcessableEntityErrorResponse(ValidationException ex) throws IOException {
        setErrorResponse(422, ex.getMessage());
    }

    public void returnUnProcessableEntityErrorResponse(String message) throws IOException {
        setErrorResponse(422, message);
    }

    public void returnInternalServerErrorResponse() throws IOException {
        setErrorResponse(500, "Internal Server Error");
    }

    public void returnOkResponse(String responseData) throws IOException {
        setResponse(200, responseData);
    }

    public void returnCreatedResponse(String responseData) throws IOException {
        setResponse(201, responseData);
    }

    public void returnNoContentResponse() throws IOException {
        setStatus(204);
        HttpServletResponse servletResponse = getHttpServletResponse();
        applyHeaders(servletResponse);
    }

    private void setErrorResponse(int statusCode, String message) throws IOException {
        setStatus(statusCode);
        JSONObject errorResponse = new JSONObject().put("message", message);
        sendResponse(errorResponse.toString());
    }

    private void setResponse(int statusCode, String responseData) throws IOException {
        setStatus(statusCode);
        sendResponse(new JSONObject(responseData).toString());
    }

    private void sendResponse(String responseData) throws IOException {
        HttpServletResponse servletResponse = getHttpServletResponse();
        applyHeaders(servletResponse);
        servletResponse.getOutputStream().write(responseData.getBytes());
    }

    private HttpServletResponse getHttpServletResponse() {
        return (HttpServletResponse) getResponse();
    }

}
