package com.rishabh.meeting.controller;

import com.rishabh.meeting.dto.CreateRoomRequest;
import com.rishabh.meeting.dto.UpdateRoomRequest;
import com.rishabh.meeting.entity.MeetingRoom;
import com.rishabh.meeting.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST endpoints for meeting-room management.
 *
 *  GET  /api/rooms                       → list ACTIVE rooms (USER + ADMIN)
 *  GET  /api/rooms/all                   → list ALL rooms    (ADMIN)
 *  GET  /api/rooms/{id}                  → get room by id    (USER + ADMIN)
 *  GET  /api/rooms/available             → availability search (USER + ADMIN)
 *  POST /api/rooms                       → create room       (ADMIN only)
 *  PUT  /api/rooms/{id}                  → update room       (ADMIN only)
 *  DELETE /api/rooms/{id}               → deactivate room   (ADMIN only)
 */
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    // ── Read ───────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<MeetingRoom>> getActiveRooms() {
        return ResponseEntity.ok(roomService.getActiveRooms());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MeetingRoom>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingRoom> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    /**
     * Availability search.
     *
     * Example:
     *  GET /api/rooms/available?startTime=2026-09-25T10:00:00&endTime=2026-09-25T11:00:00&capacity=8
     */
    @GetMapping("/available")
    public ResponseEntity<List<MeetingRoom>> getAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int capacity) {

        return ResponseEntity.ok(
                roomService.getAvailableRooms(startTime, endTime, capacity));
    }

    // ── Write (ADMIN only — also enforced at SecurityConfig level) ─────

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MeetingRoom> createRoom(
            @Valid @RequestBody CreateRoomRequest request) {

        MeetingRoom created = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MeetingRoom> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoomRequest request) {

        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    /**
     * Soft-delete: sets room status to INACTIVE.
     * Returns 200 with a message (not 204) so Postman shows clear feedback.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deactivateRoom(@PathVariable Long id) {
        roomService.deactivateRoom(id);
        return ResponseEntity.ok(Map.of("message", "Room deactivated successfully"));
    }

    /**
     * Reactivates an INACTIVE room.
     */
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> activateRoom(@PathVariable Long id) {
        roomService.activateRoom(id);
        return ResponseEntity.ok(Map.of("message", "Room activated successfully"));
    }
}
