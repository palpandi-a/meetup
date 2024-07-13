package com.service;

import com.entities.Group;
import com.entities.User;
import com.exception.ValidationException;
import com.persistence.group.GroupPersistenceAPI;
import com.persistence.group.GroupPersistenceAPIImpl;
import com.persistence.user.UserPersistenceAPI;
import com.persistence.user.UserPersistenceAPIImpl;
import com.util.TransactionUtil;
import com.validators.GroupValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GroupService {

    private final GroupPersistenceAPI groupPersistenceAPI;

    private final UserPersistenceAPI userPersistenceAPI;

    private final GroupValidator validator;

    public GroupService() {
        groupPersistenceAPI = new GroupPersistenceAPIImpl();
        userPersistenceAPI = new UserPersistenceAPIImpl();
        validator = new GroupValidator();
    }

    public Group create(Group group) throws Exception {
        validator.validateGroupDetails(group);
        List<Integer> userIds = group.getMembers().stream().map(User::getId).collect(Collectors.toList());
        Group groupDetails = new Group.Builder(group)
                .createdTime(LocalDateTime.now())
                .members(convertUserIdsIntoUserObject(userIds))
                .modifiedTime(LocalDateTime.now())
                .build();
        return TransactionUtil.execute(session -> groupPersistenceAPI.save(session, groupDetails));
    }

    public Group get(int id) throws Exception {
        return TransactionUtil.execute(session -> groupPersistenceAPI.get(session, id));
    }

    public Group update(Group group) throws Exception {
        if (group.getName() == null) {
            throw new ValidationException("'name' is a mandatory field");
        }
        Group existingGroupDetails = get(group.getId());
        if (existingGroupDetails == null) {
            throw new ValidationException("Invalid group id: " + group.getId());
        }
        Group groupDetails = constructGroupInstanceWithUpdatedData(existingGroupDetails, group);
        return TransactionUtil.execute(session -> groupPersistenceAPI.update(session, groupDetails));
    }

    public void delete(int groupId) throws Exception {
        Group existingGroup = get(groupId);
        validateGroup(existingGroup, groupId);
        TransactionUtil.execute(session -> {
            groupPersistenceAPI.delete(session, existingGroup);
            return null;
        });
    }

    public List<Group> getAll(int limit) throws Exception {
        return TransactionUtil.execute(session -> groupPersistenceAPI.getAll(session, limit));
    }

    private List<User> convertUserIdsIntoUserObject(List<Integer> userIds) throws Exception {
        return TransactionUtil.execute(session -> userPersistenceAPI.getUserDetailsByIds(session, userIds));
    }

    private Group constructGroupInstanceWithUpdatedData(Group existingObj, Group newObj) throws Exception {
        Group.Builder builder = new Group.Builder(existingObj);
        if (newObj.getName() != null) {
            builder.name(newObj.getName());
        }
        if (newObj.getMembers() != null) {
            List<Integer> userIds = newObj.getMembers().stream().map(User::getId).collect(Collectors.toList());
            List<User> members = convertUserIdsIntoUserObject(userIds);
            builder.members(members);
        }
        builder.modifiedTime(LocalDateTime.now());
        return builder.build();
    }

    public List<Group> getUserAssociatedGroups(int userId) throws Exception {
        return TransactionUtil.execute(session -> groupPersistenceAPI.getUserAssociatedGroups(session, userId));
    }

    private void validateGroup(Group group, int groupId) throws ValidationException {
        if (group == null) {
            throw new ValidationException("Invalid group id: " + groupId);
        }
    }

    public void join(int groupId, int userId) throws Exception {
        Group group = get(groupId);
        validateGroup(group, groupId);
        List<User> members = group.getMembers();
        Optional<Integer> user = members.stream().map(User::getId).filter(id -> userId == id).findFirst();
        if (user.isPresent()) {
            return;
        }
        members.add(convertUserIdsIntoUserObject(List.of(userId)).getFirst());
        Group updatedGroup = new Group.Builder(group).members(members).modifiedTime(LocalDateTime.now()).build();
        TransactionUtil.execute(session -> groupPersistenceAPI.update(session, updatedGroup));
    }

    public void leave(int groupId, int userId) throws Exception {
        Group group = get(groupId);
        validateGroup(group, groupId);
        List<User> members = group.getMembers();
        members = members.stream().filter(user -> user.getId() != userId).collect(Collectors.toList());
        Group updatedGroup = new Group.Builder(group).members(members).modifiedTime(LocalDateTime.now()).build();
        TransactionUtil.execute(session -> groupPersistenceAPI.update(session, updatedGroup));
    }

}
