package com.persistence.event;

import com.entities.Event;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class EventPersistenceAPIImpl implements EventPersistenceAPI {

    @Override
    public Event save(Session session, Event event) {
        session.persist(event);
        return get(session, event.getId());
    }

    @Override
    public Event update(Session session, Event event) {
        return session.merge(event);
    }

    @Override
    public Event get(Session session, int id) {
        return session.get(Event.class, id);
    }

    @Override
    public void delete(Session session, Event event) {
        session.remove(event);
    }

    @Override
    public List<Event> getAll(Session session, int limit) {
        Query<Event> query = session.createQuery("From Event", Event.class);
        query.setMaxResults(limit);
        return query.list();
    }

    @Override
    public List<Event> getUserAssociatedEvents(Session session, int userId) {
        String queryString = "select e from Event e join e.attendees a where e.createdBy.id = :userId or a.id = :userId";
        Query<Event> query = session.createQuery(queryString, Event.class);
        query.setParameter("userId", userId);
        return query.list();
    }

}
