package com.servlets.error.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;

public class ErrorHandlerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleError(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleError(request, response);
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        response.setHeader("Content-Type", "application/json");
        if (statusCode == 405) {
            response.getOutputStream()
                    .write(new JSONObject().put("message", "Invalid HTTP method for the request").toString().getBytes());
        } else {
            response.getOutputStream()
                    .write(new JSONObject().put("message", "Invalid request").toString().getBytes());
        }
    }
}
