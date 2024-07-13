package com.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_user_name", columnList = "name"),
                @Index(name = "idx_user_email", columnList = "email")
        }
)
public class User extends Creator {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "modified_time", nullable = false)
    private LocalDateTime modifiedTime;

    @ManyToMany(mappedBy = "members", cascade = {CascadeType.REMOVE})
    private List<Group> groups;

    @ManyToMany(mappedBy = "attendees")
    private List<Event> events;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Authorization authorization;

    protected User() {
        super();
    }

    protected User(int id) {
        super(id);
    }

    private User(Builder builder) {
        super(builder);
        this.name = builder.name;
        this.email = builder.email;
        this.password = builder.password;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.groups = builder.groups;
        this.authorization = builder.authorization;
    }

    public static class Builder extends Creator.Builder<Builder> {

        private String name;

        private String email;

        private String password;

        private LocalDateTime createdTime;

        private LocalDateTime modifiedTime;

        private List<Group> groups;

        private List<Event> events;

        private Authorization authorization;

        public Builder() {
            super();
            super.creatorType(CreatorType.USER);
        }

        public Builder(User user) {
            super(user);
            this.name = user.name;
            this.email = user.email;
            this.password = user.password;
            this.createdTime = user.createdTime;
            this.modifiedTime = user.modifiedTime;
            this.groups = user.groups;
            this.events = user.events;
            this.authorization = user.authorization;
        }

        @JsonProperty("name")
        public Builder name(String name) {
            this.name = name;
            return self();
        }

        @JsonProperty("email")
        public Builder email(String email) {
            this.email = email;
            return self();
        }

        @JsonProperty("password")
        public Builder password(String password) {
            this.password = password;
            return self();
        }

        public Builder createdTime(LocalDateTime createdTime) {
            this.createdTime = createdTime;
            return self();
        }

        public Builder modifiedTime(LocalDateTime modifiedTime) {
            this.modifiedTime = modifiedTime;
            return self();
        }

        public Builder groups(List<Group> groups) {
            this.groups = groups;
            return self();
        }

        public Builder events(List<Event> events) {
            this.events = events;
            return self();
        }

        public Builder authorization(Authorization authorization) {
            this.authorization = authorization;
            return self();
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public User build() {
            return new User(this);
        }

    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty("createdTime")
    public String getCreatedTime() {
        return createdTime == null ? null : DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm").format(createdTime);
    }

    @JsonProperty("modifiedTime")
    public String getModifiedTime() {
        return modifiedTime == null ? null : DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm").format(modifiedTime);
    }

    @JsonIgnore
    public List<Group> getGroups() {
        return groups == null ? Collections.emptyList() : groups;
    }

    @JsonIgnore
    public List<Event> getEvents() {
        return events == null ? Collections.emptyList() : events;
    }

    @JsonIgnore
    public Authorization getAuthorization() {
        return authorization;
    }
}
