package com.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Entity
@Table(
        name = "users_group",
        indexes = {
                @Index(name = "idx_group_name", columnList = "name"),
                @Index(name = "idx_group_created_by", columnList = "created_by")
        }
)
public class Group extends Creator {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_by", nullable = false)
    private Integer createdBy;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "modified_time", nullable = false)
    private LocalDateTime modifiedTime;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE})
    @JoinTable(
            name = "users_group_relation",
            joinColumns = @JoinColumn(
                    name = "group_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "user_id"
            )
    )
    private List<User> members;

    protected Group() {
        super();
    }

    private Group(Builder builder) {
        super(builder);
        this.name = builder.name;
        this.createdBy = builder.createdBy;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.members = builder.members;
    }

    public static class Builder extends Creator.Builder<Builder> {

        private String name;

        private Integer createdBy;

        private LocalDateTime createdTime;

        private LocalDateTime modifiedTime;

        private List<User> members;

        public Builder() {
            super();
            super.creatorType(CreatorType.GROUP);
        }

        public Builder(Group group) {
            super(group);
            this.name = group.name;
            this.createdBy = group.createdBy;
            this.createdTime = group.createdTime;
            this.modifiedTime = group.modifiedTime;
            this.members = group.members;
        }

        @JsonProperty("name")
        public Builder name(String name) {
            this.name = name;
            return self();
        }

        @JsonProperty("createdBy")
        public Builder createdBy(Integer createdBy) {
            this.createdBy = createdBy;
            return self();
        }

        @JsonProperty("members")
        public Builder members(List<User> members) {
            this.members = members;
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

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Group build() {
            return new Group(this);
        }

    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("createdBy")
    public Integer getCreatedBy() {
        return createdBy;
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
    public List<User> getMembers() {
        return members != null ? members : Collections.emptyList();
    }

    @JsonProperty("members")
    public List<Integer> getMembersIds() {
        return members == null ? Collections.emptyList() : members.stream().map(User::getId).collect(toList());
    }
}
