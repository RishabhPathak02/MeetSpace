import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserDto, RegisterRequest } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class AdminUsersService {

  constructor(private http: HttpClient) { }

  getAllUsers(): Observable<UserDto[]> {
    return this.http.get<UserDto[]>('/api/admin/users');
  }

  createAdmin(data: RegisterRequest): Observable<{ message: string }> {
    return this.http.post<{ message: string }>('/api/admin/users/create', data);
  }
}
