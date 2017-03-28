package com.ase_1617.organizedlib.network;

import com.ase_1617.organizedlib.data.CalEvent;

import java.util.ArrayList;

/**
 * Interface to provide information on fetched event data
 * when new information is fetched
 */

public interface AcceptEventAsyncInterface {

    void eventAccepted(Integer eventPosition);

    void eventDeclined(Integer eventPosition);
}
