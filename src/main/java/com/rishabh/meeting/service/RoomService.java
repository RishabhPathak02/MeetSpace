package com.rishabh.meeting.service;

import com.rishabh.meeting.dto.CreateRoomRequest;
import com.rishabh.meeting.dto.UpdateRoomRequest;
import com.rishabh.meeting.entity.MeetingRoom;
import com.rishabh.meeting.entity.RoomStatus;
import com.rishabh.meeting.exception.RoomNotFoundException;
import com.rishabh.meeting.repository.MeetingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final MeetingRoomRepository roomRepository;

    // ── Read ───────────────────────────────────────────────────────────

    /** Returns all ACTIVE rooms — used by USER to browse available rooms. */
    public List<MeetingRoom> getActiveRooms() {
        return roomRepository.findByStatus(RoomStatus.ACTIVE);
    }

    /** Returns ALL rooms (ACTIVE + INACTIVE) — used by ADMIN dashboard. */
    public List<MeetingRoom> getAllRooms() {
        return roomRepository.findAll();
    }

    public MeetingRoom getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));
    }

    /**
     * Room availability search.
     * Delegates the overlap logic to the repository JPQL query.
     */
    public List<MeetingRoom> getAvailableRooms(
            LocalDateTime startTime,
            LocalDateTime endTime,
            int capacity) {

        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException(
                    "startTime must be before endTime");
        }
        return roomRepository.findAvailableRooms(startTime, endTime, capacity);
    }

    // ── Write (ADMIN) ──────────────────────────────────────────────────

    @Transactional
    public MeetingRoom createRoom(CreateRoomRequest request) {

        MeetingRoom room = MeetingRoom.builder()
                .name(request.getName())
                .location(request.getLocation())
                .floor(request.getFloor())
                .capacity(request.getCapacity())
                .status(RoomStatus.ACTIVE)
                .build();

        return roomRepository.save(room);
    }

    @Transactional
    public MeetingRoom updateRoom(Long id, UpdateRoomRequest request) {

        MeetingRoom room = getRoomById(id);

        // Partial update — only overwrite fields that were supplied
        if (request.getName()     != null) room.setName(request.getName());
        if (request.getLocation() != null) room.setLocation(request.getLocation());
        if (request.getFloor()    != null) room.setFloor(request.getFloor());
        if (request.getCapacity() != null) room.setCapacity(request.getCapacity());

        return roomRepository.save(room);
    }

    @Transactional
    public void deactivateRoom(Long id) {
        MeetingRoom room = getRoomById(id);
        room.setStatus(RoomStatus.INACTIVE);
        roomRepository.save(room);
    }

    @Transactional
    public void activateRoom(Long id) {
        MeetingRoom room = getRoomById(id);
        room.setStatus(RoomStatus.ACTIVE);
        roomRepository.save(room);
    }
}
