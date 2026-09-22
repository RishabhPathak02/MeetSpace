package com.rishabh.meeting.exception;

public class RoomNotAvailableException extends RuntimeException {

    public RoomNotAvailableException() {
        super("Room is already reserved for the requested time slot");
    }

    public RoomNotAvailableException(String message) {
        super(message);
    }
}
