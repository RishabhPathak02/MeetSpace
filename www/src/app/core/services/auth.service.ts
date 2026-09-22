import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse, RegisterRequest } from '../models/models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'meetspace_token';
  private readonly USER_KEY  = 'meetspace_user';

  // Reactive signals for auth state
  private _user = signal<LoginResponse | null>(this.loadUser());

  readonly currentUser = this._user.asReadonly();
  readonly isLoggedIn  = computed(() => this._user() !== null);
  readonly isAdmin     = computed(() => this._user()?.role === 'ADMIN');

  constructor(private http: HttpClient, private router: Router) {}

  register(data: RegisterRequest): Observable<{ message: string }> {
    return this.http.post<{ message: string }>('/api/auth/register', data);
  }

  login(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/api/auth/login', data).pipe(
      tap(res => {
        localStorage.setItem(this.TOKEN_KEY, res.token);
        localStorage.setItem(this.USER_KEY, JSON.stringify(res));
        this._user.set(res);
      })
    );
  }

  updateProfile(name: string, profilePicture?: string): Observable<{ message: string }> {
    return this.http.put<{ message: string }>('/api/users/me', { name, profilePicture }).pipe(
      tap(() => {
        const currentUser = this._user();
        if (currentUser) {
          const updatedUser = { ...currentUser, name };
          if (profilePicture) updatedUser.profilePicture = profilePicture;
          localStorage.setItem(this.USER_KEY, JSON.stringify(updatedUser));
          this._user.set(updatedUser);
        }
      })
    );
  }

  deleteProfile(password: string): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>('/api/users/me', { body: { password } }).pipe(
      tap(() => {
        this.logout();
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this._user.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private loadUser(): LoginResponse | null {
    try {
      const raw = localStorage.getItem(this.USER_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  }
}
