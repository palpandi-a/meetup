package com.servlets;

import com.exception.ValidationException;
import com.io.wrapper.CustomResponseWrapper;
import com.util.TransactionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestProcessor {

    private static final Logger LOGGER = Logger.getLogger(RequestProcessor.class.getName());

    public void processRequest(HttpServletRequest request, HttpServletResponse response, TransactionHandler handler) throws IOException {
        CustomResponseWrapper responseWrapper = new CustomResponseWrapper(response);
        try {
            handler.handle(request, responseWrapper);
        } catch (ValidationException ex) {
            LOGGER.log(Level.SEVERE, "Validation exception: ", ex);
            responseWrapper.returnUnProcessableEntityErrorResponse(ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Exception: ", ex);
            responseWrapper.returnInternalServerErrorResponse();
        }
    }

    public List<Integer> getIdsFromRequestPathParam(HttpServletRequest request) {
        String[] pathInfo = request.getPathInfo().split("/");
        List<Integer> ids = new ArrayList<>();
        for (String pathIds : pathInfo) {
            if (!pathIds.isEmpty() && isNumber(pathIds)) {
                ids.add(Integer.parseInt(pathIds));
            }
        }
        return ids;
    }

    private boolean isNumber(String paramValue) {
        char[] chars = paramValue.toCharArray();
        for (char c : chars) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

}
