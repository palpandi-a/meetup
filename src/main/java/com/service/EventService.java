package com.service;

import com.entities.Creator;
import com.entities.Event;
import com.entities.EventStatus;
import com.entities.User;
import com.exception.ValidationException;
import com.persistence.creator.CreatorPersistenceAPIImpl;
import com.persistence.event.EventPersistenceAPI;
import com.persistence.event.EventPersistenceAPIImpl;
import com.persistence.user.UserPersistenceAPI;
import com.persistence.user.UserPersistenceAPIImpl;
import com.util.TransactionUtil;
import com.validators.EventValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventService {

    private EventPersistenceAPI eventPersistenceAPI;

    private UserPersistenceAPI userPersistenceAPI;

    private SchedulerService schedulerService;

    private CreatorPersistenceAPIImpl creatorPersistenceAPI;

    private EventValidator eventValidator;

    public EventService() {
        eventPersistenceAPI = new EventPersistenceAPIImpl();
        userPersistenceAPI = new UserPersistenceAPIImpl();
        schedulerService = new SchedulerService();
        creatorPersistenceAPI = new CreatorPersistenceAPIImpl();
        eventValidator = new EventValidator();
    }

    public Event create(Event event) throws Exception {
        eventValidator.validateEventDetails(event);
        Creator createdBy = getCreatorDetailsById(event.getCreatedById());
        eventValidator.validateCreatorDetails(createdBy);
        Event eventDetails = new Event.Builder(event).createdBy(createdBy).createdTime(LocalDateTime.now())
                .modifiedTime(LocalDateTime.now()).build();
        Event createdEvent = TransactionUtil.execute(session -> eventPersistenceAPI.save(session, eventDetails));
        schedulerService.scheduleEmailEvent(createdEvent);
        return createdEvent;
    }

    public Event update(Event event) throws Exception {
        eventValidator.validateUpdatingEventDetails(event);
        Event existingEventDetails = get(event.getId());
        Event eventDetails = constructEventInstanceWithUpdatedData(existingEventDetails, event);
        Event updatedEvent = TransactionUtil.execute(session -> eventPersistenceAPI.update(session, eventDetails));
        if (!Objects.equals(existingEventDetails.getEventTime(), eventDetails.getEventTime())) {
            schedulerService.scheduleEventUpdateEmail(existingEventDetails, updatedEvent);
        }
        return updatedEvent;
    }

    private Event constructEventInstanceWithUpdatedData(Event existingObj, Event newObj) throws Exception {
        Event.Builder builder = new Event.Builder(existingObj);
        if (newObj.getTitle() != null) {
            builder.title(newObj.getTitle());
        }
        if (newObj.getAttendees() != null) {
            List<Integer> userIds = newObj.getAttendees().stream().map(User::getId).collect(Collectors.toList());
            List<User> attendees = convertUserIdsIntoUserObject(userIds);
            builder.attendees(attendees);
        }
        builder.modifiedTime(LocalDateTime.now());
        return builder.build();
    }

    private List<User> convertUserIdsIntoUserObject(List<Integer> userIds) throws Exception {
        return TransactionUtil.execute(session -> userPersistenceAPI.getUserDetailsByIds(session, userIds));
    }

    public Event get(int id) throws Exception {
        Event event = TransactionUtil.execute(session -> eventPersistenceAPI.get(session, id));
        if (event == null) {
            throw new ValidationException("Invalid eventId: " + id);
        }
        return event;
    }

    public void delete(int id) throws Exception {
        Event event = get(id);
        TransactionUtil.execute(session -> {
            eventPersistenceAPI.delete(session, event);
            return null;
        });
    }

    public List<Event> getAll(int limit) throws Exception {
        return TransactionUtil.execute(session -> eventPersistenceAPI.getAll(session, limit));
    }

    private Creator getCreatorDetailsById(int creatorId) throws Exception {
        return TransactionUtil.execute(session -> creatorPersistenceAPI.getCreatorById(session, creatorId));
    }

    public void registerEvent(int eventId, int userId) throws Exception {
        Event existingEvent = get(eventId);
        List<User> attendees = existingEvent.getAttendees();
        Optional<User> isUserAlreadyRegister = attendees.stream().filter(user -> user.getId() == userId).findFirst();
        if (isUserAlreadyRegister.isPresent()) {
            return;
        }
        User user = convertUserIdsIntoUserObject(List.of(userId)).getFirst();
        attendees.add(user);
        Event event = new Event.Builder(existingEvent)
                .attendees(attendees)
                .build();
        TransactionUtil.execute(session -> eventPersistenceAPI.update(session, event));
    }

    public void cancelEvent(int eventId) throws Exception {
        Event existingEvent = get(eventId);
        if (existingEvent.getEventStatus() == EventStatus.CANCEL) {
            throw new ValidationException("Event is already cancelled. eventId: " + existingEvent.getId());
        }
        Event event = new Event.Builder(existingEvent).eventStatus(EventStatus.CANCEL).build();
        TransactionUtil.execute(session -> eventPersistenceAPI.update(session, event));
        schedulerService.scheduleEventCancelEmail(existingEvent);
    }

    public void unregisterEvent(int eventId, int userId) throws Exception {
        Event existingEvent = get(eventId);
        List<User> attendees = existingEvent.getAttendees();
        Optional<User> isUserAlreadyRegister = attendees.stream().filter(user -> user.getId() == userId).findFirst();
        if (!isUserAlreadyRegister.isPresent()) {
            return;
        }
        List<User> updatedAttendess = attendees.stream().filter(user -> user.getId() != userId).toList();
        Event event = new Event.Builder(existingEvent)
                .attendees(updatedAttendess)
                .build();
        TransactionUtil.execute(session -> eventPersistenceAPI.update(session, event));
    }

    public List<Event> getUserAssociatedEvents(int userId) throws Exception {
        return TransactionUtil.execute(session -> eventPersistenceAPI.getUserAssociatedEvents(session, userId));
    }

    public Event updateEventStatus(int eventId, EventStatus eventStatus) throws Exception {
        Event event = get(eventId);
        if (event == null) {
            throw new ValidationException("Invalid event id: " + eventId);
        }
        Event updatedEvent = new Event.Builder(event).eventStatus(eventStatus).build();
        return TransactionUtil.execute(session -> eventPersistenceAPI.update(session, updatedEvent));
    }

}
