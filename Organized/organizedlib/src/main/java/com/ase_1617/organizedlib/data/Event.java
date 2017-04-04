package com.ase_1617.organizedlib.data;

import java.util.Date;

/**
 * A data structure class for the organized Event object.
 * It contains the event name, last changed-, start- and end date, location
 * description.
 */

public class Event {
    private String eventName;
    private Date eventLastChanged;
    private Date eventStartDate;
    private Date eventEndDate;
    private String eventLocation;
    private String eventDescription;
    private String eventNotes;
    private String eventTown;

    /**Constructors*/

    public Event(String name, Date lastChanged, Date startDate, Date endDate, String location, String description/*, String notes, String town*/){
        eventStartDate = startDate;
        eventEndDate = endDate;
        eventName = name;
        eventDescription = description;
        eventLastChanged = lastChanged;
        eventLocation = location;
        /*eventNotes = notes;
        eventTown = town;*/
    }

    public Event(String name, Date lastChanged, Long startDateLong, Long endDateLong, String location, String description/*, String notes, String town*/){
        Date startDate = new Date();
        startDate.setTime(startDateLong);
        Date endDate = new Date();
        endDate.setTime(endDateLong);
        eventStartDate = startDate;
        eventEndDate = endDate;
        eventName = name;
        eventDescription = description;
        eventLastChanged = lastChanged;
        eventLocation = location;
        /*eventNotes = notes;
        eventTown = town;*/
    }

    /**Getter and setter methods*/
    
    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Date getEventLastChanged() {
        return eventLastChanged;
    }

    public void setEventLastChanged(Date eventLastChanged) {
        this.eventLastChanged = eventLastChanged;
    }

    public Date getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(Date eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public Date getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(Date eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventNotes() {
        return eventNotes;
    }

    public void setEventNotes(String eventNotes) {
        this.eventNotes = eventNotes;
    }

    public String getEventTown() {
        return eventTown;
    }

    public void setEventTown(String eventTown) {
        this.eventTown = eventTown;
    }
}
