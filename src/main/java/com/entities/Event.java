package com.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private Creator createdBy;

    @Transient
    private int createdById;

    @Column(name = "event_start_time", nullable = false)
    private LocalDateTime eventTime;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "modified_time")
    private LocalDateTime modifiedTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "event_participants",
            joinColumns = @JoinColumn(
                    name = "event_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "user_id"
            )
    )
    private List<User> attendees;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "event_status")
    private EventStatus eventStatus = EventStatus.NOT_STARTED;

    protected Event() {

    }

    public Event(Integer id, String title, String description, Creator createdBy, int createdById, LocalDateTime eventTime, LocalDateTime createdTime, LocalDateTime modifiedTime, List<User> attendees, EventStatus eventStatus) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.createdById = createdById;
        this.eventTime = eventTime;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.attendees = attendees;
        this.eventStatus = eventStatus;
    }

    public static class Builder {

        private Integer id;

        private String title;

        private String description;

        private Creator createdBy;

        private LocalDateTime eventTime;

        private LocalDateTime createdTime;

        private LocalDateTime modifiedTime;

        private List<User> attendees;

        private int createdById;

        private EventStatus eventStatus = EventStatus.NOT_STARTED;

        public Builder() {

        }

        public Builder(Event event) {
            this.id = event.id;
            this.title = event.title;
            this.description = event.description;
            this.createdBy = event.createdBy;
            this.eventTime = event.eventTime;
            this.createdTime = event.createdTime;
            this.modifiedTime = event.modifiedTime;
            this.attendees = event.attendees;
            this.createdById = event.createdById;
            this.eventStatus = event.eventStatus;
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        @JsonProperty("title")
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        @JsonProperty("description")
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder createdBy(Creator createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        @JsonProperty("createdBy")
        public Builder createdBy(int createdBy) {
            this.createdById = createdBy;
            return this;
        }

        @JsonProperty("eventTime")
        public Builder eventTime(String eventTime) {
            String[] split = eventTime.split("-");
            int day = Integer.parseInt(split[0]);
            int month = Integer.parseInt(split[1]);
            int year = Integer.parseInt(split[2].substring(0, 4));
            int hour = Integer.parseInt(split[2].substring(5, 7));
            int minute = Integer.parseInt(split[2].substring(8, 10));
            this.eventTime = LocalDateTime.of(year, month, day, hour, minute);
            return this;
        }

        public Builder eventTime(LocalDateTime eventTime) {
            this.eventTime = eventTime;
            return this;
        }

        public Builder createdTime(LocalDateTime createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public Builder modifiedTime(LocalDateTime modifiedTime) {
            this.modifiedTime = modifiedTime;
            return this;
        }

        @JsonProperty("attendees")
        public Builder attendees(List<User> attendees) {
            this.attendees = attendees;
            return this;
        }

        public Builder eventStatus(EventStatus eventStatus) {
            this.eventStatus = eventStatus;
            return this;
        }

        public Event build() {
            return new Event(id, title, description, createdBy, createdById, eventTime, createdTime, modifiedTime, attendees, eventStatus);
        }
    }

    public int getId() {
        return id;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("createdBy")
    public Creator getCreatedBy() {
        return createdBy;
    }

    @JsonIgnore
    public int getCreatedById() {
        return createdBy != null ? createdBy.getId() : createdById;
    }

    @JsonProperty("eventTime")
    public String getEventTime() {
        return eventTime == null ? null : DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm").format(eventTime);
    }

    @JsonProperty("createdTime")
    public String getCreatedTime() {
        return createdTime == null ? null : DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm").format(createdTime);
    }

    @JsonProperty("modifiedTime")
    public String getModifiedTime() {
        return modifiedTime == null ? null : DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm").format(modifiedTime);
    }

    @JsonProperty("attendees")
    public List<User> getAttendees() {
        return attendees != null ? attendees : Collections.emptyList();
    }

    @JsonProperty("eventStatus")
    public EventStatus getEventStatus() {
        return eventStatus;
    }
}
