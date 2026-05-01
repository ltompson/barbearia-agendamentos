import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Bloqueio {
  id?: number;
  data: string;
  horario: string;
  barbeiroId: number;
  motivo?: string;
}

@Injectable({ providedIn: 'root' })
export class BloqueioService {

  private apiUrl = 'http://localhost:8080/api/bloqueios';

  constructor(private http: HttpClient) {}

  criar(bloqueio: Bloqueio): Observable<any> {
    return this.http.post(this.apiUrl, bloqueio);
  }

  listarPorData(data: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}?data=${data}`);
  }

  deletar(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
