import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateRoomRequest, MeetingRoom, UpdateRoomRequest } from '../models/models';

@Injectable({ providedIn: 'root' })
export class RoomService {
  constructor(private http: HttpClient) {}

  getActiveRooms(): Observable<MeetingRoom[]> {
    return this.http.get<MeetingRoom[]>('/api/rooms');
  }

  getAllRooms(): Observable<MeetingRoom[]> {
    return this.http.get<MeetingRoom[]>('/api/rooms/all');
  }

  getRoomById(id: number): Observable<MeetingRoom> {
    return this.http.get<MeetingRoom>(`/api/rooms/${id}`);
  }

  getAvailableRooms(startTime: string, endTime: string, capacity: number): Observable<MeetingRoom[]> {
    const params = new HttpParams()
      .set('startTime', startTime)
      .set('endTime', endTime)
      .set('capacity', capacity.toString());
    return this.http.get<MeetingRoom[]>('/api/rooms/available', { params });
  }

  createRoom(data: CreateRoomRequest): Observable<MeetingRoom> {
    return this.http.post<MeetingRoom>('/api/rooms', data);
  }

  updateRoom(id: number, data: UpdateRoomRequest): Observable<MeetingRoom> {
    return this.http.put<MeetingRoom>(`/api/rooms/${id}`, data);
  }

  deactivateRoom(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`/api/rooms/${id}`);
  }

  activateRoom(id: number): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`/api/rooms/${id}/activate`, {});
  }
}
