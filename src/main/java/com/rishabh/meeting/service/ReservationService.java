package com.rishabh.meeting.service;

import com.rishabh.meeting.dto.CreateReservationRequest;
import com.rishabh.meeting.dto.ReservationResponse;
import com.rishabh.meeting.entity.*;
import com.rishabh.meeting.exception.ReservationNotFoundException;
import com.rishabh.meeting.exception.RoomNotAvailableException;
import com.rishabh.meeting.exception.RoomNotFoundException;
import com.rishabh.meeting.repository.MeetingRoomRepository;
import com.rishabh.meeting.repository.ReservationRepository;
import com.rishabh.meeting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository  reservationRepository;
    private final MeetingRoomRepository  roomRepository;
    private final UserRepository         userRepository;
    private final EmailService           emailService;

    // ── Create ─────────────────────────────────────────────────────────

    /**
     * Core booking algorithm:
     *
     *   1. Validate startTime < endTime
     *   2. Find room — 404 if missing
     *   3. Room must be ACTIVE — 409 if inactive
     *   4. Check overlap via DB query — 409 if conflict
     *   5. Persist reservation
     *
     * @Transactional ensures the overlap check and insert are atomic.
     * MVP-level concurrency protection; production would add a DB exclusion
     * constraint on (room_id, tsrange(start_time, end_time)).
     */
    @Transactional
    public ReservationResponse createReservation(
            CreateReservationRequest request,
            String userEmail) {

            // Step 1 — time sanity check
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }

        // Step 2 — find room
        MeetingRoom room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException(request.getRoomId()));

        // Step 3 — room must be active
        if (room.getStatus() != RoomStatus.ACTIVE) {
            throw new RoomNotAvailableException(
                    "Room '" + room.getName() + "' is currently INACTIVE and cannot be booked");
        }


        // Step 4 — overlap check
        boolean overlaps = reservationRepository.existsOverlappingReservation(
                room.getId(),
                request.getStartTime(),
                request.getEndTime()
        );
        if (overlaps) {
            throw new RoomNotAvailableException();
        }

        // Step 5 — load current user and save
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        Reservation reservation = Reservation.builder()
                .user(user)
                .room(room)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .purpose(request.getPurpose())
                .status(ReservationStatus.CONFIRMED)
                .build();

        Reservation saved = reservationRepository.save(reservation);
        return ReservationResponse.from(saved);
    }

    // ── Read ───────────────────────────────────────────────────────────

    /** Returns all reservations for the logged-in user, newest first. */
    @Transactional(readOnly = true)
    public List<ReservationResponse> getMyReservations(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        return reservationRepository.findByUserIdOrderByStartTimeDesc(user.getId())
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    /** Returns a single reservation by ID — throws 404 if not found. */
    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
        return ReservationResponse.from(r);
    }

    /** Returns all reservations (admin view), newest first. */
    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAllByOrderByStartTimeDesc()
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    // ── Cancel ─────────────────────────────────────────────────────────

    /**
     * Soft-cancel: CONFIRMED → CANCELLED.
     * Only the owner of the reservation (or an admin) should call this;
     * authorization is enforced at the controller level.
     */
    @Transactional
    public ReservationResponse cancelReservation(Long id, String userEmail) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        User requestor = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        // Users can only cancel their own reservations; admins can cancel any
        boolean isAdmin = requestor.getRole() == Role.ADMIN;
        boolean isOwner = reservation.getUser().getId().equals(requestor.getId());

        if (!isOwner && !isAdmin) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You can only cancel your own reservations");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalArgumentException("Reservation is already cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation saved = reservationRepository.save(reservation);
        return ReservationResponse.from(saved);
    }
    // ── Complete & Upcoming ────────────────────────────────────────────

    /**
     * Marks a reservation as COMPLETED.
     * Only the owner of the reservation (or an admin) can do this.
     */
    @Transactional
    public ReservationResponse completeReservation(Long id, String userEmail) {

        // 1. Fetch Reservation
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        // 2. Fetch User & check permissions
        User requestor = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        boolean isAdmin = requestor.getRole() == Role.ADMIN;
        boolean isOwner = reservation.getUser().getId().equals(requestor.getId());

        if (!isOwner && !isAdmin) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You can only complete your own reservations");
        }

        // 3. Validation
        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new IllegalArgumentException("Reservation is already completed");
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot complete a cancelled reservation");
        }

        // 4. Update status, save, and return
        reservation.setStatus(ReservationStatus.COMPLETED);
        Reservation saved = reservationRepository.save(reservation);
        return ReservationResponse.from(saved);
    }

    /**
     * Fetches all upcoming reservations (status is NOT COMPLETED and starts in the future).
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> getUpcomingReservations() {
        return reservationRepository.findByStatusNotAndStartTimeAfter(
                        ReservationStatus.COMPLETED,
                        java.time.LocalDateTime.now()
                )
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public ReservationResponse transferBooking(Long id, Long newUserId) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
                
        User newUser = userRepository.findById(newUserId)
                .orElseThrow(() -> new UsernameNotFoundException("User with ID not found: " + newUserId));
                
        reservation.setUser(newUser);
        Reservation saved = reservationRepository.save(reservation);

        // Notify the new owner by email (async — does not block response)
        emailService.sendBookingTransferEmail(
                newUser.getEmail(),
                newUser.getName(),
                saved.getRoom().getName(),
                saved.getStartTime().toString(),
                saved.getEndTime().toString(),
                saved.getPurpose()
        );

        return ReservationResponse.from(saved);
    }

}
