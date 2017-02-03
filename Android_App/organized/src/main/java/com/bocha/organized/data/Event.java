package com.bocha.organized.data;

import java.util.Date;

/**
 * Created by oCocha on 31.01.2017.
 */

public class Event {
    private Date eventStartDate;
    private Date eventEndDate;
    private String eventName;
    private String eventDescription;
    private Integer eventId;

    public Event(Date startDate, Date endDate, String name, String description/*, Integer Id*/){
        eventStartDate = startDate;
        eventEndDate = endDate;
        eventName = name;
        eventDescription = description;
        //eventId = Id;
    }

    public Event(Long startDateLong, Long endDateLong, String name, String description/*, Integer Id*/){
        Date startDate = new Date();
        startDate.setTime(startDateLong);
        Date endDate = new Date();
        endDate.setTime(endDateLong);
        eventStartDate = startDate;
        eventEndDate = endDate;
        eventName = name;
        eventDescription = description;
        //eventId = Id;
    }

    public Date getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(Date eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public Date getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(Date eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Integer getEventId(){
        return eventId;
    }
}
