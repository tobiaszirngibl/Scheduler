package com.bocha.organized.network;

import com.bocha.organized.data.Event;

import java.util.ArrayList;

/**
 * Created by bob on 02.02.17.
 */

public interface JSONAsyncInterface {

    void newEventsFetched(ArrayList<Event> newEventsList);
}
