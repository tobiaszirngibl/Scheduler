package com.bocha.organized.data;

import java.util.Date;

/**
 * Created by bob on 03.02.17.
 */

public class CalEvent extends Event {
    private Integer eventId;


    public CalEvent(String name, Date lastChanged, Date startDate, Date endDate, String location, String description/*, String notes, String town*/, Integer id) {
        super(name, lastChanged, startDate, endDate, location, description/*, notes, town*/);
        eventId = id;
    }

    public CalEvent(String name, Date lastChanged, Long startDate, Long endDate, String location, String description/*, String notes, String town*/, Integer id) {
        super(name, lastChanged, startDate, endDate, location, description/*, notes, town*/);
        eventId = id;
    }

    public Integer getEventId(){
        return eventId;
    }
}
