import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DiaDisponivel {
  id?: number;
  data: string;
  barbeiroId: number;
  motivo?: string;
}

@Injectable({ providedIn: 'root' })
export class DiaDisponivelService {

  private apiUrl = 'http://localhost:8080/api/dias-disponiveis';
  private agendamentoUrl = 'http://localhost:8080/api/agendamentos';

  constructor(private http: HttpClient) {}

  criar(dia: DiaDisponivel): Observable<any> {
    return this.http.post(this.apiUrl, dia);
  }

  listarPorData(data: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}?data=${data}`);
  }

  deletar(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  isDiaDisponivel(data: string, barbeiroId: number): Observable<boolean> {
    return this.http.get<boolean>(
      `${this.agendamentoUrl}/dia-disponivel`,
      { params: { data, barbeiroId: barbeiroId.toString() } }
    );
  }
}
