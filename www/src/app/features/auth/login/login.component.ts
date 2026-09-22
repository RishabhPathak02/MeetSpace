import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { AnimatedBorderComponent } from '../../../shared/utility/animated-border/animated-border.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, AnimatedBorderComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  form = { email: '', password: '' };
  loading = false;
  error = '';
  showPass = false;

  constructor(private authService: AuthService, private router: Router) {}

  fillAdmin() { this.form = { email: 'admin@meeting.com', password: 'Admin@123' }; }

  onLogin() {
    if (!this.form.email || !this.form.password) return;
    this.loading = true;
    this.error = '';
    this.authService.login(this.form).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.error = err.error?.message ?? 'Invalid email or password';
        this.loading = false;
      },
    });
  }
}
