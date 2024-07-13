package com.service;

import com.entities.Event;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerService {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private EmailService emailService;

    public SchedulerService() {
        emailService = new EmailService();
    }

    public void scheduleEmailEvent(Event event) {
        String body = new StringBuilder("The ").append(event.getTitle()).append(" event is starting soon!").toString();
        LocalDateTime eventTime = constructTimeObj(event.getEventTime());
        LocalDateTime now = LocalDateTime.now();

        long initialDelay;
        if (eventTime.isBefore(now.plusHours(1))) {
            initialDelay = eventTime.minusMinutes(30).toEpochSecond(ZoneOffset.UTC) - now.toEpochSecond(ZoneOffset.UTC);
        } else {
            initialDelay = eventTime.minusHours(2).toEpochSecond(ZoneOffset.UTC) - now.toEpochSecond(ZoneOffset.UTC);
        }

        if (initialDelay > 0) {
            scheduler.schedule(() -> sendEmail(event, body), initialDelay, TimeUnit.SECONDS);
        }
    }

    public void scheduleEventUpdateEmail(Event oldEvent, Event event) {
        String body = new StringBuilder("The ").append(event.getTitle()).append(" event timing was changed from ")
                .append(oldEvent.getEventTime()).append(" to ").append(event.getEventTime()).append(" !")
                .toString();
        scheduler.schedule(() -> sendEmail(event, body), 0, TimeUnit.SECONDS);
    }

    public void scheduleEventCancelEmail(Event event) {
        String body = new StringBuilder("The ").append(event.getTitle())
                .append(" event was cancelled!").toString();
        scheduler.schedule(() -> sendEmail(event, body), 0, TimeUnit.SECONDS);
    }

    private void sendEmail(Event event, String body) {
        event.getAttendees().forEach(attendees -> emailService.sendEmail(attendees.getEmail(), "Event Reminder", body));
    }

    private LocalDateTime constructTimeObj(String eventTime) {
        String[] split = eventTime.split("-");
        int day = Integer.parseInt(split[0]);
        int month = Integer.parseInt(split[1]);
        int year = Integer.parseInt(split[2].substring(0, 4));
        int hour = Integer.parseInt(split[2].substring(5, 7));
        int minute = Integer.parseInt(split[2].substring(8, 10));
        return LocalDateTime.of(year, month, day, hour, minute);
    }

}
