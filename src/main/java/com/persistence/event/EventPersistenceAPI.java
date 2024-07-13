package com.persistence.event;

import com.entities.Event;
import com.persistence.api.PersistenceAPI;
import org.hibernate.Session;

import java.util.List;

public interface EventPersistenceAPI extends PersistenceAPI<Event> {

    List<Event> getAll(Session session, int limit);

    List<Event> getUserAssociatedEvents(Session session, int userId);

}
