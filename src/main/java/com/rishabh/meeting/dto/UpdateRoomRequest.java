package com.rishabh.meeting.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

/**
 * All fields are optional — only provided fields are updated (partial update).
 */
@Data
public class UpdateRoomRequest {

    private String name;

    private String location;

    @PositiveOrZero(message = "Floor must be >= 0")
    private Integer floor;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
}
