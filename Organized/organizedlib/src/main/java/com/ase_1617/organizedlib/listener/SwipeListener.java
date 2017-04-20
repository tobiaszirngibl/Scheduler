package com.ase_1617.organizedlib.listener;

import android.view.MotionEvent;
import android.view.View;

/**
 * Listener class which extends the OnTouchListener to detect swipe actions
 */

public class SwipeListener implements View.OnTouchListener{

    /**
     * Enum declaring the available swipe actions
     */

    public enum Action{
        LR, // Left to Right
        RL, // Right to Left
        TB, // Top to bottom
        BT, // Bottom to Top
        None // when no action was detected
    }

    private static final int MIN_DISTANCE = 100;
    private float downX, downY, upX, upY;
    private Action mSwipeDetected = Action.None;

    /**
     * Swipe helper functions
     */

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    /**
     *
     * Check for a swipe action and set the mSwipeDetected variable accordingly.
     *
     * @param v view element the swipe is performed on
     * @param event event element to be checked for swipe actions
     * @return whether the event was handled already or can be processed furthermore
     */
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                mSwipeDetected = Action.None;
                return false; //Allow other events like Click to be processed
            }
            case MotionEvent.ACTION_MOVE: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                //Horizontal swipe detection
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    //Left or right
                    if (deltaX < 0) {
                        mSwipeDetected = Action.LR;
                        return true;
                    }
                    if (deltaX > 0) {
                        mSwipeDetected = Action.RL;
                        return true;
                    }
                } else

                    //Vertical swipe detection
                    if (Math.abs(deltaY) > MIN_DISTANCE) {
                        //Top or down
                        if (deltaY < 0) {
                            mSwipeDetected = Action.TB;
                            return false;
                        }
                        if (deltaY > 0) {
                            mSwipeDetected = Action.BT;
                            return false;
                        }
                    }
                return true;
            }
        }
        return false;
    }
}
