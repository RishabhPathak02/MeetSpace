import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RoomService } from '../../../core/services/room.service';
import { ReservationService } from '../../../core/services/reservation.service';
import { MeetingRoom } from '../../../core/models/models';

@Component({
  selector: 'app-room-search',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './room-search.component.html',
  styleUrl: './room-search.component.css'
})
export class RoomSearchComponent {
  private roomService = inject(RoomService);
  private reservationService = inject(ReservationService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  today = new Date().toISOString().split('T')[0];

  searchForm = this.fb.group({
    date: [this.today, Validators.required],
    startTime: ['10:00', Validators.required],
    endTime: ['11:00', Validators.required],
    capacity: [1, [Validators.required, Validators.min(1)]],
  });

  bookingPurposeCtrl = this.fb.control('');

  formError = signal('');
  searching = signal(false);
  searched = signal(false);
  availableRooms = signal<MeetingRoom[]>([]);

  bookingRoomId = signal<number | null>(null);
  booking = signal(false);
  bookingError = signal('');
  bookingSuccess = signal('');

  constructor() {
    this.searchForm.get('startTime')?.valueChanges.subscribe(val => {
      if (!val) return;
      const [h, m] = val.split(':').map(Number);
      const endH = (h + 1) % 24;
      this.searchForm.patchValue({
        endTime: `${endH.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}`
      });
    });

    this.searchForm.get('endTime')?.valueChanges.subscribe(val => {
      const start = this.searchForm.value.startTime;
      if (start && val && val < start) {
        this.searchForm.patchValue({ endTime: start }, { emitEvent: false });
      }
    });
  }

  search() {
    this.formError.set('');
    if (this.searchForm.invalid) {
      this.formError.set('Please fill in date, start time and end time.');
      return;
    }

    const { date, startTime, endTime, capacity } = this.searchForm.value;
    const start = new Date(`${date}T${startTime}:00`);
    const end   = new Date(`${date}T${endTime}:00`);

    if (start >= end) {
      this.formError.set('End time must be after start time.');
      return;
    }

    this.searching.set(true);
    const startISO = `${date}T${startTime}:00`;
    const endISO   = `${date}T${endTime}:00`;

    this.roomService.getAvailableRooms(startISO, endISO, capacity!).subscribe({
      next: (rooms) => {
        this.availableRooms.set(rooms);
        this.searched.set(true);
        this.searching.set(false);
      },
      error: () => {
        this.searching.set(false);
        this.formError.set('Search failed. Is the backend running?');
      },
    });
  }

  startBooking(roomId: number) {
    this.bookingRoomId.set(roomId);
    this.bookingError.set('');
    this.bookingSuccess.set('');
    this.bookingPurposeCtrl.setValue('');
  }

  confirmBooking(room: MeetingRoom) {
    this.booking.set(true);
    this.bookingError.set('');

    const { date, startTime, endTime } = this.searchForm.value;
    const start = `${date}T${startTime}:00`;
    const end   = `${date}T${endTime}:00`;

    this.reservationService.createReservation({
      roomId: room.id,
      startTime: start,
      endTime: end,
      purpose: this.bookingPurposeCtrl.value || undefined,
    }).subscribe({
      next: () => {
        this.bookingSuccess.set(`✅ ${room.name} booked for ${this.formatTime(startTime!)}–${this.formatTime(endTime!)}`);
        this.booking.set(false);
        this.availableRooms.update(rooms => rooms.filter(r => r.id !== room.id));
        setTimeout(() => this.router.navigate(['/reservations']), 1800);
      },
      error: (err) => {
        console.log('Booking error:', err);
        this.bookingError.set(err.error?.message ?? 'Booking failed.');
        this.booking.set(false);
      },
    });
  }

  formatDate(d: string) {
    return new Date(d).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
  }
  formatTime(t: string) {
    const [h, m] = t.split(':').map(Number);
    const ampm = h >= 12 ? 'PM' : 'AM';
    return `${h % 12 || 12}:${m.toString().padStart(2, '0')} ${ampm}`;
  }
}

