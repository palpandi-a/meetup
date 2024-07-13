package com.servlets;

import com.entities.Event;
import com.entities.EventStatus;
import com.entities.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.io.wrapper.CustomResponseWrapper;
import com.service.EventService;
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

public class EventServlet extends HttpServlet {

    private EventService eventService;

    private ObjectMapper mapper;

    private RequestProcessor requestProcessor;

    @Override
    public void init() throws ServletException {
        eventService = new EventService();
        mapper = new ObjectMapper();
        requestProcessor = new RequestProcessor();
    }

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
        String apiPath = request.getPathInfo();
        if (apiPath != null) {
            String endPoint = apiPath.substring(apiPath.lastIndexOf('/') + 1);
            switch (endPoint) {
                case "register":
                    handleRegisterEventRequest(request, responseWrapper);
                    break;
                case "cancel":
                    handleEventCancelRequest(request, responseWrapper);
                    break;
                case "unregister":
                    handleUnRegisterEventRequest(request, responseWrapper);
                    break;
                case "start":
                    handleEventStatusRequest(request, responseWrapper, EventStatus.IN_PROGRESS);
                    break;
                case "complete":
                    handleEventStatusRequest(request, responseWrapper, EventStatus.COMPLETED);
                    break;
                default:
                    responseWrapper.returnBadRequestResponse("Invalid POST request");
            }
        } else {
            handleEventCreateRequest(request, responseWrapper);
        }
    }

    private void handleEventCreateRequest(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        Event event = mapper.readValue(request.getInputStream(), Event.Builder.class).build();
        event = eventService.create(event);
        responseWrapper.returnCreatedResponse(constructResponse(event).toString());
    }

    private void handleRegisterEventRequest(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        int eventId = requestProcessor.getIdsFromRequestPathParam(request).getFirst();
        int userId = new JSONObject(new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8)).getInt("userId");
        eventService.registerEvent(eventId, userId);
        responseWrapper.returnNoContentResponse();
    }

    private void handleUnRegisterEventRequest(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        int eventId = requestProcessor.getIdsFromRequestPathParam(request).getFirst();
        int userId = new JSONObject(new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8)).getInt("userId");
        eventService.unregisterEvent(eventId, userId);
        responseWrapper.returnNoContentResponse();
    }

    private void handleEventCancelRequest(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        int eventId = requestProcessor.getIdsFromRequestPathParam(request).getFirst();
        eventService.cancelEvent(eventId);
        responseWrapper.returnNoContentResponse();
    }

    private void handleEventStatusRequest(HttpServletRequest request, CustomResponseWrapper responseWrapper, EventStatus eventStatus) throws Exception {
        int eventId = requestProcessor.getIdsFromRequestPathParam(request).getFirst();
        Event event = eventService.updateEventStatus(eventId, eventStatus);
        responseWrapper.returnOkResponse(constructResponse(event).toString());
    }

    private void handleGet(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        if (request.getPathInfo().split("/").length > 2) {
            responseWrapper.returnBadRequestResponse("Invalid GET request");
            return;
        }
        Event event = eventService.get(requestProcessor.getIdsFromRequestPathParam(request).getFirst());
        responseWrapper.returnOkResponse(constructResponse(event).toString());
    }

    private void handleGetAll(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        int limit = Optional.ofNullable(request.getParameter("limit")).map(Integer::parseInt).orElse(200);
        List<Event> eventList = eventService.getAll(limit);
        responseWrapper.returnOkResponse(new JSONObject().put("events", constructListResponse(eventList)).toString());
    }

    private void handleDelete(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        eventService.delete(requestProcessor.getIdsFromRequestPathParam(request).getFirst());
        responseWrapper.returnNoContentResponse();
    }

    private void handlePut(HttpServletRequest request, CustomResponseWrapper responseWrapper) throws Exception {
        Event event = mapper.readValue(request.getInputStream(), Event.Builder.class)
                .id(requestProcessor.getIdsFromRequestPathParam(request).getFirst())
                .build();
        event = eventService.update(event);
        responseWrapper.returnOkResponse(constructResponse(event).toString());
    }

    private JSONArray constructListResponse(List<Event> eventList) throws JsonProcessingException {
        JSONArray response = new JSONArray();
        for (Event event : eventList) {
            response.put(constructResponse(event));
        }
        return response;
    }

    private JSONObject constructResponse(Event event) throws JsonProcessingException {
        JSONObject response = new JSONObject(mapper.writeValueAsString(event));
        removeAdditionalKeysFromCreatedByObject(response.getJSONObject("createdBy"));
        removeAdditionalKeysFromAttendessObject(response.getJSONArray("attendees"));
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
