package com.example.kalendrium;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

public class CreateEvent {
    public CreateEvent() {}

    public static void addEvent(String file, VEvent event) {
        try {
            FileInputStream fin = new FileInputStream(file);
            Calendar calendar = new CalendarBuilder().build(fin);
            calendar.getComponents().add(event);
            FileOutputStream fout = new FileOutputStream(file);
            fout.write(calendar.toString().getBytes());
            fout.close();
        } catch (IOException | ParserException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addNoneOverlapingEvent(String file, VEvent eventToAdd) {
        try {
            FileInputStream fin = new FileInputStream(file);
            Calendar calendar = new CalendarBuilder().build(fin);

            List<VEvent> events = calendar.getComponents(Component.VEVENT);
            boolean overlapping = false;

            for (VEvent existingEvent : events) {
                Date existingEnd = existingEvent.getEndDate().getDate();
                Date existingStart = existingEvent.getStartDate().getDate();

                Date newStart = eventToAdd.getStartDate().getDate();
                Date newEnd = eventToAdd.getEndDate().getDate();

                if (newStart.compareTo(existingEnd) < 0 && newEnd.compareTo(existingStart) > 0) {
                    overlapping = true;
                    break;
                }
            }

            if (!overlapping) {
                calendar.getComponents().add(eventToAdd);
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(calendar.toString().getBytes());
                fout.close();
            } else {
                System.out.println("Error: The new event overlaps with an existing event.");
            }

        } catch (IOException | ParserException e) {
            throw new RuntimeException(e);
        }
    }

    public static VEvent createEvent(DateTime dtStart, DateTime dtEnd, String summary, String description, String location) {
        VEvent event = new VEvent();

        event.getProperties().add(new DtStart(dtStart));
        event.getProperties().add(new DtEnd(dtEnd));
        event.getProperties().add(new Summary(summary));
        event.getProperties().add(new Location(location));
        event.getProperties().add(new Description(description));

        return event;
    }

    public static DateTime createDateTime(int year, int month, int day, int hour, int minute) {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(year, month, day, hour, minute, 0, 0, ZoneId.systemDefault());
        return new DateTime(zonedDateTime.toInstant().toEpochMilli());
    }
}
