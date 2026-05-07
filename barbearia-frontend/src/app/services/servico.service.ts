import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Servico {
  id?: number;
  nome: string;
  preco: number;
  duracaoMinutos: number;
  ativo: boolean;
}

@Injectable({ providedIn: 'root' })
export class ServicoService {

  private apiUrl = 'http://localhost:8080/api/servicos';

  constructor(private http: HttpClient) { }

  // Apenas ativos — tela de agendamento
  listarAtivos(): Observable<Servico[]> {
    return this.http.get<Servico[]>(this.apiUrl);
  }

  // Todos — painel admin
  listarTodos(): Observable<Servico[]> {
    return this.http.get<Servico[]>(`${this.apiUrl}/todos`);
  }

  criar(servico: Servico): Observable<Servico> {
    return this.http.post<Servico>(this.apiUrl, servico);
  }

  atualizar(id: number, servico: Servico): Observable<Servico> {
    return this.http.put<Servico>(`${this.apiUrl}/${id}`, servico);
  }

  toggleAtivo(id: number): Observable<Servico> {
    return this.http.patch<Servico>(`${this.apiUrl}/${id}/toggle`, {});
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
