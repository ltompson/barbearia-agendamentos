import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Barbeiro {
  id?: number;
  nome: string;
  telefone: string;
  ativo: boolean;
}

@Injectable({ providedIn: 'root' })
export class BarbeiroService {

  private apiUrl = 'http://localhost:8080/api/barbeiros';

  constructor(private http: HttpClient) {}

  listarAtivos(): Observable<Barbeiro[]> {
    return this.http.get<Barbeiro[]>(this.apiUrl);
  }

  listarTodos(): Observable<Barbeiro[]> {
    return this.http.get<Barbeiro[]>(`${this.apiUrl}/todos`);
  }

  criar(barbeiro: Barbeiro): Observable<Barbeiro> {
    return this.http.post<Barbeiro>(this.apiUrl, barbeiro);
  }

  atualizar(id: number, barbeiro: Barbeiro): Observable<Barbeiro> {
    return this.http.put<Barbeiro>(`${this.apiUrl}/${id}`, barbeiro);
  }

  toggleAtivo(id: number): Observable<Barbeiro> {
    return this.http.patch<Barbeiro>(`${this.apiUrl}/${id}/toggle`, {});
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
