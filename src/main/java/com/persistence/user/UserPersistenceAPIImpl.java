package com.persistence.user;

import com.entities.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class UserPersistenceAPIImpl implements UserPersistenceAPI {

    public User save(Session session, User user) {
        session.persist(user);
        return get(session, user.getId());
    }

    public User get(Session session, int userId) {
        return session.get(User.class, userId);
    }

    @Override
    public User update(Session session, User user) {
        return session.merge(user);
    }

    @Override
    public void delete(Session session, User user) {
        session.remove(user);
    }

    @Override
    public User getUserDetailsByEmail(Session session, String email) {
        String queryString = "From User where email = :email";
        Query<User> query = session.createQuery(queryString, User.class);
        query.setParameter("email", email);
        return query.uniqueResult();
    }

    @Override
    public List<User> getUserDetailsByIds(Session session, List<Integer> userIds) throws Exception {
        String queryString = "From User where id in ( :id )";
        Query<User> query = session.createQuery(queryString, User.class);
        query.setParameter("id", userIds);
        return query.list();
    }

}
