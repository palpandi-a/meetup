package com.persistence.creator;

import com.entities.Creator;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class CreatorPersistenceAPIImpl {

    public Creator getCreatorById(Session session, int id) {
        String queryString = "From Creator c where c.id = :id";
        Query<Creator> query = session.createQuery(queryString, Creator.class);
        query.setParameter("id", id);
        return query.uniqueResult();
    }

}
