package com.persistence.authorization;

import com.entities.Authorization;
import org.hibernate.Session;

public class AuthorizationPersistenceAPIImpl implements AuthorizationPersistenceAPI {

    @Override
    public Authorization save(Session session, Authorization authorization) {
        session.persist(authorization);
        return get(session, authorization.getId());
    }

    @Override
    public Authorization update(Session session, Authorization authorization) {
        return session.merge(authorization);
    }

    @Override
    public Authorization get(Session session, int id) {
        return session.get(Authorization.class, id);
    }

    @Override
    public void delete(Session session, Authorization authorization) {
        session.remove(authorization);
    }

}
