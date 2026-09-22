import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  private authService = inject(AuthService);
  
  user = this.authService.currentUser;
  
  isEditingName = false;
  editNameValue = '';
  editProfilePictureBase64?: string;
  loading = false;
  successMessage = '';
  errorMessage = '';
  showDangerZone = false;
  confirmDelete = false;
  deletePassword = '';

  ngOnInit(): void {
    const current = this.user();
    if (current) {
      this.editNameValue = current.name;
    }
  }

  startEditName() {
    this.isEditingName = true;
    this.successMessage = '';
    this.errorMessage = '';
    const current = this.user();
    if (current) {
      this.editNameValue = current.name;
      this.editProfilePictureBase64 = current.profilePicture;
    }
  }

  cancelEditName() {
    this.isEditingName = false;
    this.editProfilePictureBase64 = undefined;
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.editProfilePictureBase64 = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  saveName() {
    if (!this.editNameValue.trim()) {
      this.errorMessage = 'Name cannot be empty';
      return;
    }
    
    this.loading = true;
    this.authService.updateProfile(this.editNameValue, this.editProfilePictureBase64).subscribe({
      next: (res) => {
        this.loading = false;
        this.isEditingName = false;
        this.successMessage = res.message || 'Profile updated successfully';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Failed to update profile';
      }
    });
  }

  deleteProfile() {
    this.confirmDelete = true;
  }

  cancelDelete() {
    this.confirmDelete = false;
    this.deletePassword = '';
  }

  executeDelete() {
    if (!this.deletePassword) {
      this.errorMessage = 'Please enter your password to delete your account.';
      setTimeout(() => this.errorMessage = '', 3000);
      return;
    }

    this.loading = true;
    this.authService.deleteProfile(this.deletePassword).subscribe({
      next: () => {
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.confirmDelete = false;
        this.errorMessage = err.error?.message || 'Failed to delete profile';
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }
}
