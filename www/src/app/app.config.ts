import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { routes } from './app.routes';
import { authInterceptor } from './core/interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    // Angular 21: withComponentInputBinding allows route params as @Input signals
    provideRouter(routes, withComponentInputBinding()),
    // Removed withFetch() because it causes infinite hangs with the Vite proxy in some environments
    provideHttpClient(withInterceptors([authInterceptor])),
    // Angular 21: async animations provider (lazy-loads animation engine)
    provideAnimationsAsync(),
  ],
};
