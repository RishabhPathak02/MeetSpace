import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReservationService } from '../../../core/services/reservation.service';
import { Reservation } from '../../../core/models/models';

@Component({
  selector: 'app-my-reservations',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-reservations.component.html',
  styleUrl: './my-reservations.component.css'
})
export class MyReservationsComponent implements OnInit {
  reservations: Reservation[] = [];
  loading = true;
  activeTab: 'all' | 'confirmed' | 'cancelled' | 'completed' | 'expired' = 'confirmed';
  cancellingId: number | null = null;
  successMsg = '';
  errorMsg = '';

  get confirmedCount()  { return this.reservations.filter(r => r.status === 'CONFIRMED').length; }
  get cancelledCount()  { return this.reservations.filter(r => r.status === 'CANCELLED').length; }
  get completedCount()  { return this.reservations.filter(r => r.status === 'COMPLETED').length; }
  get expiredCount()    { return this.reservations.filter(r => r.status === 'EXPIRED').length; }

  get filteredReservations() {
    if (this.activeTab === 'confirmed') return this.reservations.filter(r => r.status === 'CONFIRMED');
    if (this.activeTab === 'cancelled') return this.reservations.filter(r => r.status === 'CANCELLED');
    if (this.activeTab === 'completed') return this.reservations.filter(r => r.status === 'COMPLETED');
    if (this.activeTab === 'expired')   return this.reservations.filter(r => r.status === 'EXPIRED');
    return this.reservations;
  }

  constructor(private reservationService: ReservationService) {}

  ngOnInit() { this.load(); }

  load() {
    this.reservationService.getMyReservations().subscribe({
      next: (data) => { this.reservations = data; this.loading = false; },
      error: () => { this.loading = false; },
    });
  }

  cancel(r: Reservation) {
    if (!confirm(`Cancel booking for ${r.roomName}?`)) return;
    this.cancellingId = r.id;
    this.reservationService.cancelReservation(r.id).subscribe({
      next: () => {
        this.cancellingId = null;
        const idx = this.reservations.findIndex(x => x.id === r.id);
        if (idx > -1) this.reservations[idx] = { ...this.reservations[idx], status: 'CANCELLED' };
        this.successMsg = 'Reservation cancelled successfully.';
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.cancellingId = null;
        this.errorMsg = err.error?.message ?? 'Failed to cancel reservation.';
        setTimeout(() => this.errorMsg = '', 4000);
      },
    });
  }

  completingId: number | null = null;
  markAsDone(r: Reservation) {
    if (!confirm(`Mark booking for ${r.roomName} as completed?`)) return;
    this.completingId = r.id;
    this.reservationService.completeReservation(r.id).subscribe({
      next: () => {
        this.completingId = null;
        const idx = this.reservations.findIndex(x => x.id === r.id);
        if (idx > -1) this.reservations[idx] = { ...this.reservations[idx], status: 'COMPLETED' };
        this.successMsg = 'Reservation marked as done.';
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.completingId = null;
        this.errorMsg = err.error?.message ?? 'Failed to complete reservation.';
        setTimeout(() => this.errorMsg = '', 4000);
      },
    });
  }

  openTransferDialog(booking: Reservation) {
    const newUserId = prompt(`Enter the user ID to transfer booking for ${booking.roomName}:`);
    if (!newUserId) return;
    
    this.reservationService.transferReservation(booking.id, parseInt(newUserId)).subscribe({
      next: () => {
        this.successMsg = 'Reservation transferred successfully.';
        setTimeout(() => this.successMsg = '', 3000);
        this.load(); 
      },
      error: (err) => {
        this.errorMsg = err.error?.message ?? 'Failed to transfer reservation.';
        setTimeout(() => this.errorMsg = '', 4000);
      },
    });
  }


  formatDate(dt: string) { return new Date(dt).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' }); }
  formatTime(dt: string) { return new Date(dt).toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit', hour12: true }); }
}
