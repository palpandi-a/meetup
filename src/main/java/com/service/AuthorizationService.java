package com.service;

import com.entities.Authorization;
import com.persistence.authorization.AuthorizationPersistenceAPI;
import com.persistence.authorization.AuthorizationPersistenceAPIImpl;
import com.util.TransactionUtil;

public class AuthorizationService {

    private AuthorizationPersistenceAPI authorizationPersistenceAPI;

    public AuthorizationService() {
        this(new AuthorizationPersistenceAPIImpl());
    }

    public AuthorizationService(AuthorizationPersistenceAPI authorizationPersistenceAPI) {
        this.authorizationPersistenceAPI = authorizationPersistenceAPI;
    }

    public Authorization add(Authorization authorization) throws Exception {
        return TransactionUtil.execute(session -> authorizationPersistenceAPI.save(session, authorization));
    }

    public void delete(Authorization authorization) throws Exception {
        TransactionUtil.execute(session -> {
            authorizationPersistenceAPI.delete(session, authorization);
            return null;
        });
    }

}
