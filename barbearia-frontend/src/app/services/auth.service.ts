import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) { }

  login(usuario: string, senha: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, { usuario, senha });
  }

  logout(): void {
    localStorage.removeItem('jwt');
    localStorage.removeItem('role');
  }

  getToken(): string | null {
    return localStorage.getItem('jwt');
  }

  isLogado(): boolean {
    return !!this.getToken();
  }

  getUsuario(): string {
    const token = this.getToken();
    if (!token) return '';
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.sub || '';
    } catch {
      return '';
    }
  }

  getRole(): string {
    return localStorage.getItem('role') || 'FUNCIONARIO';
  }

  getBarbeiroId(): number | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.barbeiroId || null;
    } catch {
      return null;
    }
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }
}






