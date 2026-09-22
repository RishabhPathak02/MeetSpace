import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoomService } from '../../../core/services/room.service';
import { MeetingRoom, CreateRoomRequest } from '../../../core/models/models';

@Component({
  selector: 'app-room-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './room-management.component.html',
  styleUrl: './room-management.component.css',
})
export class RoomManagementComponent implements OnInit {
  rooms: MeetingRoom[] = [];
  loading = true;
  showForm = false;
  submitting = false;
  formError = '';
  formSuccess = '';
  deactivatingId: number | null = null;
  editingRoom: MeetingRoom | null = null;

  form: CreateRoomRequest = { name: '', location: '', floor: 1, capacity: 10 };

  constructor(private roomService: RoomService) {}

  ngOnInit() { this.load(); }

  load() {
    this.roomService.getAllRooms().subscribe({
      next: (data) => { this.rooms = data; this.loading = false; },
      error: () => { this.loading = false; },
    });
  }

  editRoom(room: MeetingRoom) {
    this.editingRoom = room;
    this.form = { name: room.name, location: room.location, floor: room.floor, capacity: room.capacity };
    this.showForm = true;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  resetForm() {
    this.form = { name: '', location: '', floor: 1, capacity: 10 };
    this.editingRoom = null; this.formError = ''; this.formSuccess = '';
  }

  submit() {
    if (!this.form.name || !this.form.location || !this.form.capacity) {
      this.formError = 'Name, location and capacity are required.'; return;
    }
    this.submitting = true; this.formError = ''; this.formSuccess = '';

    const req = this.editingRoom
      ? this.roomService.updateRoom(this.editingRoom.id, this.form)
      : this.roomService.createRoom(this.form);

    req.subscribe({
      next: (room) => {
        this.formSuccess = this.editingRoom ? 'Room updated!' : `Room "${room.name}" created!`;
        this.submitting = false;
        this.resetForm();
        this.load();
        setTimeout(() => { this.formSuccess = ''; this.showForm = false; }, 1500);
      },
      error: (err) => { this.formError = err.error?.message ?? 'Operation failed.'; this.submitting = false; },
    });
  }

  deactivate(room: MeetingRoom) {
    this.deactivatingId = room.id;
    this.roomService.deactivateRoom(room.id).subscribe({
      next: () => { 
        this.deactivatingId = null; 
        this.formSuccess = `Room "${room.name}" deactivated!`;
        this.load(); 
        setTimeout(() => { this.formSuccess = ''; }, 3000);
      },
      error: (err) => { 
        this.deactivatingId = null; 
        this.formError = err.error?.message || 'Failed to deactivate room';
      },
    });
  }

  activate(room: MeetingRoom) {
    this.deactivatingId = room.id;
    this.roomService.activateRoom(room.id).subscribe({
      next: () => { 
        this.deactivatingId = null; 
        this.formSuccess = `Room "${room.name}" activated!`;
        this.load(); 
        setTimeout(() => { this.formSuccess = ''; }, 3000);
      },
      error: (err) => { 
        this.deactivatingId = null; 
        this.formError = err.error?.message || 'Failed to activate room';
      },
    });
  }
}
