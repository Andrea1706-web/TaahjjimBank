import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:3000/auth';

  constructor(private http: HttpClient) { }

  login(credentials: { email: string; password: string }): Observable<boolean> {
    return this.http.post<boolean>(`${this.apiUrl}/login`, credentials);
  }
}
