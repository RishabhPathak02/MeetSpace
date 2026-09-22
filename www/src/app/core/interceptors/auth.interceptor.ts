import { inject } from '@angular/core';
import { HttpInterceptorFn } from '@angular/common/http';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(AuthService).getToken();
  
  let headers = req.headers;
  if (token) {
    headers = headers.set('Authorization', `Bearer ${token}`);
  }

  const url = req.url.startsWith('/api') ? `http://localhost:8080${req.url}` : req.url;

  const clonedReq = req.clone({ url, headers });

  return next(clonedReq);
};
