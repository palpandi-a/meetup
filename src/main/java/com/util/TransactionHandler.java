package com.util;

import com.io.wrapper.CustomResponseWrapper;
import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface TransactionHandler {
    void handle(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception;
}
