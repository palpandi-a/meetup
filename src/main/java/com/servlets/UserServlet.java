package com.servlets;

import com.entities.Event;
import com.entities.Group;
import com.entities.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.io.wrapper.CustomResponseWrapper;
import com.service.EventService;
import com.service.GroupService;
import com.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class UserServlet extends HttpServlet {

    private UserService service;

    private ObjectMapper mapper;

    private RequestProcessor requestProcessor;

    private GroupService groupService;

    private EventService eventService;

    @Override
    public void init() throws ServletException {
        service = new UserService();
        mapper = new ObjectMapper();
        requestProcessor = new RequestProcessor();
        groupService = new GroupService();
        eventService = new EventService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requestProcessor.processRequest(request, response, this::handleGet);
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requestProcessor.processRequest(request, response, this::handlePut);
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requestProcessor.processRequest(request, response, this::handleDelete);
    }

    private void handleGet(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            responseWrapper.returnBadRequestResponse("Invalid GET request");
            return;
        }
        if (pathInfo.matches("/\\d*/groups")) {
            handleUserAssociatedGroupsGettingRequest(request, responseWrapper);
        } else if (pathInfo.matches("/\\d*/events")) {
            handleUserAssociatedEventsGettingRequest(request, responseWrapper);
        } else {
            User user = service.get(requestProcessor.getIdsFromRequestPathParam(request).getFirst());
            responseWrapper.returnOkResponse(mapper.writeValueAsString(user));
        }
    }

    private void handleUserAssociatedGroupsGettingRequest(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        int userId = requestProcessor.getIdsFromRequestPathParam(request).getFirst();
        List<Group> associatedGroups = groupService.getUserAssociatedGroups(userId);
        JSONObject responseData = new JSONObject();
        responseData.put("groups", constructGroupListResponse(associatedGroups));
        responseWrapper.returnOkResponse(responseData.toString());
    }

    private void handleUserAssociatedEventsGettingRequest(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        int userId = requestProcessor.getIdsFromRequestPathParam(request).getFirst();
        List<Event> associatedEvents = eventService.getUserAssociatedEvents(userId);
        JSONObject responseData = new JSONObject();
        responseData.put("events", this.constructEventListResponse(associatedEvents));
        responseWrapper.returnOkResponse(responseData.toString());
    }

    private void handlePut(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        User user = mapper.readValue(request.getInputStream(), User.Builder.class)
                .id(requestProcessor.getIdsFromRequestPathParam(request).getFirst()).build();
        user = service.update(user);
        responseWrapper.returnOkResponse(mapper.writeValueAsString(user));
    }

    private void handleDelete(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        int userId = requestProcessor.getIdsFromRequestPathParam(request).getFirst();
        service.delete(userId);
        HttpSession session = request.getSession(false);
        session.invalidate();
        responseWrapper.returnNoContentResponse();
    }

    private JSONArray constructGroupListResponse(List<Group> groupList) throws JsonProcessingException {
        JSONArray response = new JSONArray();
        for (Group group : groupList) {
            response.put(new JSONObject(mapper.writeValueAsString(group)));
        }
        return response;
    }

    private JSONArray constructEventListResponse(List<Event> eventList) throws JsonProcessingException {
        JSONArray response = new JSONArray();
        for (Event event : eventList) {
            JSONObject responseData = new JSONObject(mapper.writeValueAsString(event));
            removeAdditionalKeysFromCreatedByObject(responseData.getJSONObject("createdBy"));
            removeAdditionalKeysFromAttendessObject(responseData.getJSONArray("attendees"));
            response.put(responseData);
        }
        return response;
    }

    private void removeAdditionalKeysFromCreatedByObject(JSONObject object) {
        object.remove("createdTime");
        object.remove("modifiedTime");
        object.remove("createdBy");
        object.remove("email");
    }

    private void removeAdditionalKeysFromAttendessObject(JSONArray attendees) {
        for (int i = 0; i < attendees.length(); i++) {
            JSONObject attendee = attendees.getJSONObject(i);
            removeAdditionalKeysFromCreatedByObject(attendee);
        }
    }

}
