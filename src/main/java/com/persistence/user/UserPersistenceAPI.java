package com.persistence.user;

import com.entities.Group;
import com.entities.User;
import com.persistence.api.PersistenceAPI;
import org.hibernate.Session;

import java.util.List;

public interface UserPersistenceAPI extends PersistenceAPI<User> {

    User getUserDetailsByEmail(Session session, String email) throws Exception;

    List<User> getUserDetailsByIds(Session session, List<Integer> userIds) throws Exception;

}
