package com.rishabh.meeting.exception;

public class RoomNotFoundException extends RuntimeException {

    public RoomNotFoundException(Long id) {
        super("Meeting room not found with id: " + id);
    }
}
