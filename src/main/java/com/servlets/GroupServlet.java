package com.servlets;

import com.entities.Group;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.io.wrapper.CustomResponseWrapper;
import com.service.GroupService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class GroupServlet extends HttpServlet {

    private GroupService groupService;

    private ObjectMapper mapper;

    private RequestProcessor requestProcessor;

    @Override
    public void init() throws ServletException {
        mapper = new ObjectMapper();
        groupService = new GroupService();
        requestProcessor = new RequestProcessor();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requestProcessor.processRequest(request, response, this::handlePost);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            requestProcessor.processRequest(request, response, this::handleGet);
        } else {
            requestProcessor.processRequest(request, response, this::handleGetAll);
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requestProcessor.processRequest(request, response, this::handlePut);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requestProcessor.processRequest(request, response, this::handleDelete);
    }

    private void handlePost(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            String endPoint = pathInfo.substring(pathInfo.lastIndexOf('/') + 1);
            switch (endPoint) {
                case "join":
                    handleJoinRequest(request, responseWrapper);
                    break;
                case "leave":
                    handleLeaveRequest(request, responseWrapper);
                    break;
                default:
                    responseWrapper.returnBadRequestResponse("Invalid PUT request");
            }
        } else {
            handleEventCreateRequest(request, responseWrapper);
        }
    }

    private void handleEventCreateRequest(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        Group group = mapper.readValue(request.getInputStream(), Group.Builder.class).build();
        group = groupService.create(group);
        responseWrapper.returnCreatedResponse(mapper.writeValueAsString(group));
    }

    private void handlePut(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        Group group = mapper.readValue(request.getInputStream(), Group.Builder.class)
                .id(requestProcessor.getIdsFromRequestPathParam(request).getFirst()).build();
        group = groupService.update(group);
        responseWrapper.returnOkResponse(mapper.writeValueAsString(group));
    }

    private void handleJoinRequest(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        int groupId = requestProcessor.getIdsFromRequestPathParam(request).getFirst();
        int userId = new JSONObject(new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8)).getInt("userId");
        groupService.join(groupId, userId);
        responseWrapper.returnNoContentResponse();
    }

    private void handleLeaveRequest(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        int groupId = requestProcessor.getIdsFromRequestPathParam(request).getFirst();
        int userId = new JSONObject(new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8)).getInt("userId");
        groupService.leave(groupId, userId);
        responseWrapper.returnNoContentResponse();
    }

    private void handleDelete(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        int groupId = requestProcessor.getIdsFromRequestPathParam(request).getFirst();
        groupService.delete(groupId);
        responseWrapper.returnNoContentResponse();
    }

    private void handleGet(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        int groupId = requestProcessor.getIdsFromRequestPathParam(request).getFirst();
        Group group = groupService.get(groupId);
        responseWrapper.returnCreatedResponse(mapper.writeValueAsString(group));
    }

    private void handleGetAll(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        int limit = Optional.ofNullable(request.getParameter("limit")).map(Integer::parseInt).orElse(200);
        List<Group> groupList = groupService.getAll(limit);
        responseWrapper.returnOkResponse(new JSONObject().put("groups", constructListResponse(groupList)).toString());
    }

    private JSONArray constructListResponse(List<Group> groupList) throws JsonProcessingException {
        JSONArray response = new JSONArray();
        for (Group group : groupList) {
            response.put(new JSONObject(mapper.writeValueAsString(group)));
        }
        return response;
    }

}
