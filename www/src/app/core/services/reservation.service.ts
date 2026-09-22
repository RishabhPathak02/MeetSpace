import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateReservationRequest, Reservation } from '../models/models';

@Injectable({ providedIn: 'root' })
export class ReservationService {
  constructor(private http: HttpClient) {}

  createReservation(data: CreateReservationRequest): Observable<Reservation> {
    return this.http.post<Reservation>('/api/reservations', data);
  }

  getMyReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>('/api/reservations/my');
  }

  getReservationById(id: number): Observable<Reservation> {
    return this.http.get<Reservation>(`/api/reservations/${id}`);
  }

  cancelReservation(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`/api/reservations/${id}`);
  }

  getAllReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>('/api/admin/reservations');
  }

  getUpcomingReservations(): Observable<Reservation[]>{
    return this.http.get<Reservation[]>('/api/reservations/upcoming');
  }

  completeReservation(id:number): Observable<Reservation>{
    return this.http.put<Reservation>(`/api/reservations/${id}/complete`,{});
  }

  transferReservation(id: number, newUserId: number): Observable<Reservation> {
    return this.http.put<Reservation>(`/api/reservations/${id}/transfer`, { newUserId });
  }
  
}
