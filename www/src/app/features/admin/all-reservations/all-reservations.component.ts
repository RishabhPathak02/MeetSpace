import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReservationService } from '../../../core/services/reservation.service';
import { Reservation } from '../../../core/models/models';

@Component({
  selector: 'app-all-reservations',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './all-reservations.component.html',
  styleUrl: './all-reservations.component.css'
})
export class AllReservationsComponent implements OnInit {
  reservations: Reservation[] = [];
  loading = true;

  get confirmedCount() { return this.reservations.filter(r => r.status === 'CONFIRMED').length; }
  get cancelledCount() { return this.reservations.filter(r => r.status === 'CANCELLED').length; }
  get completedCount() { return this.reservations.filter(r => r.status === 'COMPLETED').length; }
  get expiredCount() { return this.reservations.filter(r => r.status === 'EXPIRED').length; }

  constructor(private reservationService: ReservationService) {}

  ngOnInit() {
    this.reservationService.getAllReservations().subscribe({
      next: (data) => { this.reservations = data; this.loading = false; },
      error: () => { this.loading = false; },
    });
  }

  formatDate(dt: string) { return new Date(dt).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' }); }
  formatTime(dt: string) { return new Date(dt).toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit', hour12: true }); }
}
