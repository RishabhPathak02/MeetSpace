package com.rishabh.meeting.repository;

import com.rishabh.meeting.entity.MeetingRoom;
import com.rishabh.meeting.entity.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {

    /** Returns all rooms that are currently ACTIVE. */
    List<MeetingRoom> findByStatus(RoomStatus status);

    /**
     * Availability search — a room is available when:
     *   1. Status is ACTIVE
     *   2. Capacity >= requested capacity
     *   3. No CONFIRMED reservation overlaps [startTime, endTime)
     *
     * Overlap condition: existing.startTime < :endTime AND existing.endTime > :startTime
     */
    @Query("""
            SELECT r FROM MeetingRoom r
            WHERE r.status = 'ACTIVE'
              AND r.capacity >= :capacity
              AND r.id NOT IN (
                  SELECT res.room.id FROM Reservation res
                  WHERE res.status = 'CONFIRMED'
                    AND res.startTime < :endTime
                    AND res.endTime   > :startTime
              )
            """)
    List<MeetingRoom> findAvailableRooms(
            @Param("startTime")  LocalDateTime startTime,
            @Param("endTime")    LocalDateTime endTime,
            @Param("capacity")   int capacity
    );
}
