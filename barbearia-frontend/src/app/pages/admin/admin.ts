import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AgendamentoService } from '../../services/agendamento';

@Component({
  selector: 'app-admin',
  imports: [CommonModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class Admin implements OnInit {

  // Controla qual seção está ativa no menu
  secaoAtiva = 'agendamentos';

  // Lista de agendamentos carregados do backend
  agendamentos: any[] = [];
  carregando = true;

  constructor(private agendamentoService: AgendamentoService) {}

  ngOnInit() {
    this.carregarAgendamentos();
  }

  // Busca todos os agendamentos no backend
  carregarAgendamentos() {
    this.carregando = true;
    this.agendamentoService.listar().subscribe({
      next: (dados) => {
        this.agendamentos = dados;
        this.carregando = false;
      },
      error: (err) => {
        console.error(err);
        this.carregando = false;
      }
    });
  }
}
