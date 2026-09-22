import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ReservationService } from '../../core/services/reservation.service';
import { RoomService } from '../../core/services/room.service';
import { Reservation, MeetingRoom } from '../../core/models/models';
import { AnimatedBorderComponent } from '../../shared/utility/animated-border/animated-border.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, AnimatedBorderComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  loading = true;
  reservations: Reservation[] = [];
  rooms: MeetingRoom[] = [];

  get upcomingReservations() {
    return this.reservations.filter(r => r.status === 'CONFIRMED').slice(0, 5);
  }
  get confirmedCount()  { return this.reservations.filter(r => r.status === 'CONFIRMED').length; }
  get cancelledCount()  { return this.reservations.filter(r => r.status === 'CANCELLED').length; }
  get totalCount()      { return this.reservations.length; }
  get activeRooms()     { return this.rooms.filter(r => r.status === 'ACTIVE').length; }

  get timeGreeting(): string {
    const h = new Date().getHours();
    if (h < 12) return '☀️ Good morning';
    if (h < 17) return '🌤️ Good afternoon';
    return '🌙 Good evening';
  }

  constructor(
    public authService: AuthService,
    private reservationService: ReservationService,
    private roomService: RoomService
  ) {}

  ngOnInit() {
    this.reservationService.getMyReservations().subscribe({
      next: (data) => { this.reservations = data; this.loading = false; },
      error: () => { this.loading = false; },
    });
    this.roomService.getActiveRooms().subscribe({
      next: (data) => this.rooms = data,
    });
  }

  formatDate(dt: string): string {
    return new Date(dt).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
  }
  formatTime(dt: string): string {
    return new Date(dt).toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit', hour12: true });
  }


}
