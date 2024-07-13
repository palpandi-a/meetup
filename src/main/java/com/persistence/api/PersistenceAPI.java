package com.persistence.api;

import org.hibernate.Session;

public interface PersistenceAPI<T> {

    T save(Session session, T entity);

    T update(Session session, T entity);

    T get(Session session, int id);

    void delete(Session session, T entity);

}
