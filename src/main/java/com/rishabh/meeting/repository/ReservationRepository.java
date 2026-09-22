package com.rishabh.meeting.repository;

import com.rishabh.meeting.entity.Reservation;
import com.rishabh.meeting.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Overlap detection query.
     *
     * Two intervals [s1, e1) and [s2, e2) overlap when:
     *   s1 < e2  AND  e1 > s2
     *
     * Returns true if any CONFIRMED booking for the given room overlaps the
     * requested window — used before creating a new reservation.
     */
    @Query("""
            SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
            FROM Reservation r
            WHERE r.room.id = :roomId
              AND r.status = 'CONFIRMED'
              AND r.startTime < :endTime
              AND r.endTime   > :startTime
            """)
    boolean existsOverlappingReservation(
            @Param("roomId")    Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime")   LocalDateTime endTime
    );

    /** All reservations belonging to a specific user, newest first. */
    List<Reservation> findByUserIdOrderByStartTimeDesc(Long userId);

    /** All reservations across all users (Admin view), newest first. */
    List<Reservation> findAllByOrderByStartTimeDesc();

    /** All reservations for a specific room (useful for debugging). */
    List<Reservation> findByRoomIdAndStatus(Long roomId, ReservationStatus status);

    /** To mark the reservation done */
    List<Reservation> findByStatusNotAndStartTimeAfter(
            ReservationStatus status,
            LocalDateTime time
    );

}
