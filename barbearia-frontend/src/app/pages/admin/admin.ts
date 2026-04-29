import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AgendamentoService } from '../../services/agendamento';

@Component({
  selector: 'app-admin',
  imports: [CommonModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class Admin implements OnInit {

  secaoAtiva = 'agendamentos';
  agendamentos: any[] = [];
  carregando = true;

  constructor(
    private agendamentoService: AgendamentoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.carregarAgendamentos();
  }

  carregarAgendamentos() {
    this.carregando = true;
    this.agendamentoService.listar().subscribe({
      next: (dados) => {
        this.agendamentos = dados;
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.carregando = false;
        this.cdr.detectChanges();
      }
    });
  }

  cancelar(id: number) {
    if (!confirm('Deseja cancelar este agendamento?')) return;
    this.agendamentoService.cancelar(id).subscribe({
      next: () => this.carregarAgendamentos(),
      error: (err) => console.error(err)
    });
  }
}
