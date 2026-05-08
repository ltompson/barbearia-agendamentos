import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Funcionario {
  id?: number;
  nome: string;
  usuario: string;
  senha?: string;
  ativo: boolean;
}

@Injectable({ providedIn: 'root' })
export class FuncionarioService {

  private apiUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  listarTodos(): Observable<Funcionario[]> {
    return this.http.get<Funcionario[]>(this.apiUrl);
  }

  criar(funcionario: Funcionario): Observable<Funcionario> {
    return this.http.post<Funcionario>(`${this.apiUrl}/cadastrar`, funcionario);
  }

  atualizar(id: number, funcionario: Funcionario): Observable<Funcionario> {
    return this.http.put<Funcionario>(`${this.apiUrl}/${id}`, funcionario);
  }

  toggleAtivo(id: number): Observable<Funcionario> {
    return this.http.patch<Funcionario>(`${this.apiUrl}/${id}/toggle`, {});
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
