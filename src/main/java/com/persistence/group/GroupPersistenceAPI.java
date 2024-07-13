package com.persistence.group;

import com.entities.Group;
import com.persistence.api.PersistenceAPI;
import org.hibernate.Session;

import java.util.List;

public interface GroupPersistenceAPI extends PersistenceAPI<Group> {

    List<Group> getAll(Session session, int limit);

    List<Group> getUserAssociatedGroups(Session session, int userId);

}
