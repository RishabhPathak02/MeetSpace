import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { AnimatedBorderComponent } from '../../../shared/utility/animated-border/animated-border.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, AnimatedBorderComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  form = { name: '', email: '', password: '', city: '' };
  loading = false;
  error = '';
  success = '';
  showPass = false;
  step = 1;

  constructor(private authService: AuthService, private router: Router) {}

  onRegister() {
    if (!this.form.name || !this.form.email || this.form.password.length < 8) return;
    this.loading = true;
    this.error = '';
    this.success = '';

    this.authService.register(this.form).subscribe({
      next: (res) => {
        this.loading = false;
        this.success = res.message + ' Redirecting to login…';
        this.step = 2;
        setTimeout(() => this.router.navigate(['/login']), 1500);
      },
      error: (err) => {
        this.error = err.error?.message ?? 'Registration failed. Try again.';
        this.loading = false;
      },
    });
  }
}
