package com.ase_1617.organizedlib.data;

import java.util.Date;

/**
 * A data structure class for the organized calEvent object.
 * It expands the organized Event and adds an id.
 */

public class CalEvent extends Event {
    private Integer eventId;

    /**Constructors*/

    public CalEvent(String name, Date lastChanged, Date startDate, Date endDate, String location, String description/*, String notes, String town*/, Integer id) {
        super(name, lastChanged, startDate, endDate, location, description/*, notes, town*/);
        eventId = id;
    }

    public CalEvent(String name, Date lastChanged, Long startDate, Long endDate, String location, String description/*, String notes, String town*/, Integer id) {
        super(name, lastChanged, startDate, endDate, location, description/*, notes, town*/);
        eventId = id;
    }

    /**Getter methods*/

    public Integer getEventId(){
        return eventId;
    }
}
