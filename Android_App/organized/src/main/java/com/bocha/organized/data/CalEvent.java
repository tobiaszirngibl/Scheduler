package com.bocha.organized.data;

import java.util.Date;

/**
 * Created by bob on 03.02.17.
 */

public class CalEvent extends Event {
    private Integer eventId;

    public CalEvent(Date startDate, Date endDate, String name, String description, Integer id) {
        super(startDate, endDate, name, description);
        eventId = id;
    }

    public CalEvent(Long startDateLong, Long endDateLong, String name, String description, Integer id) {
        super(startDateLong, endDateLong, name, description);
        eventId = id;
    }

    public Integer getEventId(){
        return eventId;
    }
}
