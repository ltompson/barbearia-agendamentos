import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


// Define o formato dos dados que serão enviados para o backend
export interface AgendamentoRequest {
  nomeCliente: string;
  telefoneCliente: string;
  barbeiroId: number;
  servicoId: number;
  dataHora: string;
}

@Injectable({
  providedIn: 'root'
})
export class AgendamentoService {

  // URL base do backend Spring Boot
  private apiUrl = 'http://localhost:8080/api/agendamentos';

  constructor(private http: HttpClient) { }

  // Envia um novo agendamento para o backend
  criar(agendamento: AgendamentoRequest): Observable<any> {
    return this.http.post(this.apiUrl, agendamento);
  }

  // Busca todos os agendamentos (usado no painel admin)
  listar(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  // Cancela um agendamento pelo ID
  cancelar(id: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/cancelar`, {});
  }

  getHorariosDisponiveis(data: Date, barbeiroId: number): Observable<string[]> {
    const dataISO = data.toISOString().split('T')[0]; // converte para YYYY-MM-DD
    return this.http.get<string[]>(`${this.apiUrl}/disponiveis`, {
      params: { data: dataISO, barbeiroId: barbeiroId.toString() }
    });
  }

}
