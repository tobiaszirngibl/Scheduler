package com.ase_1617.organizedlib.network;


/**
 * Interface to provide information on the position of the handled event in the
 * event list when an event was accepted or declined.
 */

public interface AcceptEventAsyncInterface {

    void eventAccepted(Integer eventPosition);

    void eventDeclined(Integer eventPosition);
}
