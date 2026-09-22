import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { RoomService } from '../../../core/services/room.service';
import { MeetingRoom } from '../../../core/models/models';

@Component({
  selector: 'app-room-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './room-list.component.html',
  styleUrl: './room-list.component.css'
})
export class RoomListComponent implements OnInit {
  rooms: MeetingRoom[] = [];
  loading = true;

  constructor(private roomService: RoomService) {}

  capacityPercent(capacity: number): number {
    return Math.min((capacity / 25) * 100, 100);
  }

  ngOnInit() {
    this.roomService.getActiveRooms().subscribe({
      next: (data) => { this.rooms = data; this.loading = false; },
      error: () => { this.loading = false; },
    });
  }
}
