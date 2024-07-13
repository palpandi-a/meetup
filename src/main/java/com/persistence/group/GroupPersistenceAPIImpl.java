package com.persistence.group;

import com.entities.Group;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class GroupPersistenceAPIImpl implements GroupPersistenceAPI {

    @Override
    public Group save(Session session, Group group) {
        session.persist(group);
        return get(session, group.getId());
    }

    @Override
    public Group update(Session session, Group group) {
        return session.merge(group);
    }

    @Override
    public Group get(Session session, int id) {
        return session.get(Group.class, id);
    }

    @Override
    public void delete(Session session, Group group) {
        session.remove(group);
    }

    @Override
    public List<Group> getAll(Session session, int limit) {
        Query<Group> query = session.createQuery("From Group", Group.class);
        query.setMaxResults(limit);
        return query.list();
    }

    @Override
    public List<Group> getUserAssociatedGroups(Session session, int userId) {
        String queryString = "select g from Group g join g.members m where g.createdBy = :userId or m.id = :userId";
        Query<Group> query = session.createQuery(queryString, Group.class);
        query.setParameter("userId", userId);
        return query.list();
    }

}
