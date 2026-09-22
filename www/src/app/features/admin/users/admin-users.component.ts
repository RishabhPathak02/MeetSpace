import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminUsersService } from '../../../core/services/admin-users.service';
import { CityService, City } from '../../../core/services/city.service';
import { UserDto, RegisterRequest } from '../../../core/models/models';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.css']
})
export class AdminUsersComponent implements OnInit {
  users = signal<UserDto[]>([]);
  cities = signal<City[]>([]);
  isLoading = signal<boolean>(true);
  error = signal<string>('');
  
  roleFilter = signal<string>('ALL');
  filteredUsers = computed(() => {
    const currentUsers = this.users();
    const currentFilter = this.roleFilter();
    if (currentFilter === 'ALL') {
      return currentUsers;
    }
    return currentUsers.filter(u => u.role === currentFilter);
  });
  
  showCreateForm = signal<boolean>(false);
  newAdmin: RegisterRequest = { name: '', email: '', password: '', city: '' };
  isSubmitting = signal<boolean>(false);

  constructor(private adminUsersService: AdminUsersService, private cityService: CityService) {}

  ngOnInit(): void {
    this.loadUsers();
    this.loadCities();
  }

  loadCities(): void {
    this.cityService.getAllCities().subscribe({
      next: (data) => this.cities.set(data),
      error: (err) => console.error('Failed to load cities', err)
    });
  }

  loadUsers(): void {
    this.isLoading.set(true);
    this.adminUsersService.getAllUsers().subscribe({
      next: (data) => {
        this.users.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load users.');
        this.isLoading.set(false);
      }
    });
  }

  toggleCreateForm(): void {
    this.showCreateForm.set(!this.showCreateForm());
    this.newAdmin = { name: '', email: '', password: '', city: '' };
  }

  createAdmin(): void {
    if (!this.newAdmin.name || !this.newAdmin.email || !this.newAdmin.password || !this.newAdmin.city) {
      this.error.set('All fields are required.');
      return;
    }

    this.isSubmitting.set(true);
    this.error.set('');

    this.adminUsersService.createAdmin(this.newAdmin).subscribe({
      next: (res) => {
        this.isSubmitting.set(false);
        this.toggleCreateForm();
        this.loadUsers(); 
      },
      error: (err) => {
        this.isSubmitting.set(false);
        this.error.set(err.error?.message || 'Failed to create admin.');
      }
    });
  }

  getInitials(name: string): string {
    if (!name) return 'U';
    const parts = name.trim().split(' ');
    if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
    return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
  }
}
