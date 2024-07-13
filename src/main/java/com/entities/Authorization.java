package com.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "user_authorization")
public class Authorization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "auth_key")
    private String authKey;

    protected Authorization() {

    }

    private Authorization(int id, User user, String authKey) {
        this.id = id;
        this.user = user;
        this.authKey = authKey;
    }

    public static class Builder {

        private int id;

        private User user;

        private String authKey;

        public Builder() {

        }

        public Builder(Authorization authorization) {
            this.id = authorization.id;
            this.user = authorization.user;
            this.authKey = authorization.authKey;
        }

        public Builder(Builder builder) {
            this.id = builder.id;
            this.user = builder.user;
            this.authKey = builder.authKey;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder authKey(String authKey) {
            this.authKey = authKey;
            return this;
        }

        public Authorization build() {
            return new Authorization(id, user, authKey);
        }
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getAuthKey() {
        return authKey;
    }
}
