import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-animated-border',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="animated-border-wrapper">
      <div class="animated-border-content">
        <ng-content></ng-content>
      </div>
    </div>
  `,
  styleUrls: ['./animated-border.component.css']
})
export class AnimatedBorderComponent {}
