import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },

  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then(m => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then(m => m.RegisterComponent),
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
  },
  {
    path: 'rooms',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/rooms/room-list/room-list.component').then(m => m.RoomListComponent),
  },
  {
    path: 'rooms/search',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/rooms/room-search/room-search.component').then(m => m.RoomSearchComponent),
  },
  {
    path: 'reservations',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/reservations/my-reservations/my-reservations.component').then(m => m.MyReservationsComponent),
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/user/profile/profile.component').then(m => m.ProfileComponent),
  },
  {
    path: 'admin/rooms',
    canActivate: [authGuard, adminGuard],
    loadComponent: () =>
      import('./features/admin/room-management/room-management.component').then(m => m.RoomManagementComponent),
  },
  {
    path: 'admin/reservations',
    canActivate: [authGuard, adminGuard],
    loadComponent: () =>
      import('./features/admin/all-reservations/all-reservations.component').then(m => m.AllReservationsComponent),
  },
  {
    path: 'admin/users',
    canActivate: [authGuard, adminGuard],
    loadComponent: () =>
      import('./features/admin/users/admin-users.component').then(m => m.AdminUsersComponent),
  },
  { path: '**', redirectTo: '/dashboard' },
];
