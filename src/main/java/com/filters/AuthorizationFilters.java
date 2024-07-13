package com.filters;

import com.entities.User;
import com.io.wrapper.CustomResponseWrapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AuthorizationFilters implements Filter {

    private static final Set<String> UNAUTHENTICATED_URLS = new HashSet<>(Arrays.asList("signup", "login"));

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        String uri = servletRequest.getRequestURI().substring(servletRequest.getRequestURI().lastIndexOf('/') + 1);
        if (!UNAUTHENTICATED_URLS.contains(uri)) {
            String authorizationHeaderValue = servletRequest.getHeader("authorization");
            if (authorizationHeaderValue == null) {
                sendErrorResponse(servletResponse, "You are not authorized to perform this operation");
                return;
            }
            HttpSession session = servletRequest.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                sendErrorResponse(servletResponse, "Please signup or login to perform this operation");
                return;
            }
            if (!Objects.equals(authorizationHeaderValue, user.getAuthorization().getAuthKey())) {
                sendErrorResponse(servletResponse, "Invalid auth value");
                return;
            }

        }
        chain.doFilter(servletRequest, response);
    }

    private void sendErrorResponse(HttpServletResponse servletResponse, String message) throws IOException {
        new CustomResponseWrapper(servletResponse)
                .returnForbiddenErrorResponse(new JSONObject().put("message", message).toString());
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
