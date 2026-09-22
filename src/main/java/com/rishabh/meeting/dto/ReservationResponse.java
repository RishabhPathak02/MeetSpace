package com.rishabh.meeting.dto;

import com.rishabh.meeting.entity.Reservation;
import com.rishabh.meeting.entity.ReservationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Flattened response DTO — avoids lazy-loading issues and leaking entity internals.
 */
@Data
@Builder
public class ReservationResponse {

    private Long              id;
    private Long              roomId;
    private String            roomName;
    private String            roomLocation;
    private Long              userId;
    private String            userName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime     startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime     endTime;
    private ReservationStatus status;
    private String            purpose;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime     createdAt;

    /** Factory method to build from a loaded Reservation entity. */
    public static ReservationResponse from(Reservation r) {
        ReservationStatus computedStatus = r.getStatus();
        if (computedStatus == ReservationStatus.CONFIRMED && r.getEndTime().isBefore(LocalDateTime.now())) {
            computedStatus = ReservationStatus.EXPIRED;
        }

        return ReservationResponse.builder()
                .id(r.getId())
                .roomId(r.getRoom().getId())
                .roomName(r.getRoom().getName())
                .roomLocation(r.getRoom().getLocation())
                .userId(r.getUser().getId())
                .userName(r.getUser().getName())
                .startTime(r.getStartTime())
                .endTime(r.getEndTime())
                .status(computedStatus)
                .purpose(r.getPurpose())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
