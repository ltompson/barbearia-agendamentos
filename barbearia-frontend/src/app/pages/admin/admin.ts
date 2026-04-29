import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { AgendamentoService } from '../../services/agendamento';

@Component({
  selector: 'app-admin',
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class Admin implements OnInit {

  secaoAtiva = 'agendamentos';
  agendamentos: any[] = [];
  agendamentosFiltrados: any[] = [];
  carregando = true;

  // Data selecionada no filtro — começa com hoje
  dataSelecionada: Date = new Date();

  constructor(
    private agendamentoService: AgendamentoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.carregarAgendamentos();
  }

  // Busca todos os agendamentos no backend
  carregarAgendamentos() {
    this.carregando = true;
    this.agendamentoService.listar().subscribe({
      next: (dados) => {
        this.agendamentos = dados;
        this.filtrarPorData();
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

  // Filtra agendamentos pela data selecionada
  filtrarPorData() {
    if (!this.dataSelecionada) {
      this.agendamentosFiltrados = this.agendamentos;
      return;
    }

    const dataSel = new Date(this.dataSelecionada);

    this.agendamentosFiltrados = this.agendamentos.filter(ag => {
      const dataAg = new Date(ag.dataHora);
      return dataAg.getFullYear() === dataSel.getFullYear() &&
             dataAg.getMonth() === dataSel.getMonth() &&
             dataAg.getDate() === dataSel.getDate();
    });

    this.cdr.detectChanges();
  }

  // Cancela um agendamento pelo ID
  cancelar(id: number) {
    if (!confirm('Deseja cancelar este agendamento?')) return;
    this.agendamentoService.cancelar(id).subscribe({
      next: () => this.carregarAgendamentos(),
      error: (err) => console.error(err)
    });
  }
}
