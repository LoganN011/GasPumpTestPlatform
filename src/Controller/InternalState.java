package Controller;

public enum InternalState {
    OFF, //pump unavailable and everything null
    STANDBY, // waiting for price list to not be null
    IDLE, //showing welcome waiting for card tap
    AUTHORIZING, //showing authorize screen
    DECLINED, // showing card declined and card number null
    SELECTION, //fuel select screen waiting for input
    ATTACHING, // waiting for hose
    FUELING, //pumping gas checking connect and full sensor, and waiting for screen input
    DETACHED, //waiting for attached or finalize
    PAUSED, // pause button clicked wait for resume or done
    DETACHING, //waiting for pump to be detached
    COMPLETE, // waiting for timer to end showing thank you
    OFF_DETACHING // waiting for detach of hose
}
