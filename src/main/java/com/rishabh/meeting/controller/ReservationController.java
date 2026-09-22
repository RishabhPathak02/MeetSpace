package com.rishabh.meeting.controller;

import com.rishabh.meeting.dto.CreateReservationRequest;
import com.rishabh.meeting.dto.ReservationResponse;
import com.rishabh.meeting.dto.TransferReservationRequest;
import com.rishabh.meeting.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST endpoints for reservations.
 *
 *  POST   /api/reservations            → create reservation  (USER + ADMIN)
 *  GET    /api/reservations/my         → my reservations     (USER + ADMIN)
 *  GET    /api/reservations/{id}       → reservation detail  (USER + ADMIN)
 *  DELETE /api/reservations/{id}       → cancel reservation  (owner or ADMIN)
 *  GET    /api/admin/reservations      → ALL reservations    (ADMIN)
 */
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // ── User endpoints ─────────────────────────────────────────────────

    @PostMapping("/api/reservations")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody CreateReservationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        ReservationResponse response = reservationService.createReservation(
                request,
                userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/reservations/my")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(
            @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("rishabh");
        return ResponseEntity.ok(
                reservationService.getMyReservations(userDetails.getUsername()));
    }

    @GetMapping("/api/reservations/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @DeleteMapping("/api/reservations/{id}")
    public ResponseEntity<Map<String, String>> cancelReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        reservationService.cancelReservation(id, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "Reservation cancelled successfully"));
    }

    @PutMapping("/api/reservations/{id}/complete")
    public ResponseEntity<ReservationResponse> completeReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        ReservationResponse response = reservationService.completeReservation(id, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/reservations/upcoming")
    public ResponseEntity<List<ReservationResponse>> getUpcomingReservations() {

        return ResponseEntity.ok(reservationService.getUpcomingReservations());
    }

    // ── Admin endpoint ─────────────────────────────────────────────────

    @GetMapping("/api/admin/reservations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @PutMapping("/api/reservations/{id}/transfer")
    public ResponseEntity<ReservationResponse> transferBooking(
            @PathVariable Long id, 
            @RequestBody TransferReservationRequest request) {
        return ResponseEntity.ok(reservationService.transferBooking(id, request.getNewUserId()));
    }
}
