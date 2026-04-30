import { Component, OnInit, ChangeDetectorRef, LOCALE_ID } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, MAT_DATE_LOCALE } from '@angular/material/core';
import { AgendamentoService } from '../../services/agendamento';
import localePt from '@angular/common/locales/pt';
import { Router } from '@angular/router';

registerLocaleData(localePt);

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
  styleUrl: './admin.css',
  providers: [
    { provide: LOCALE_ID, useValue: 'pt-BR' },
    { provide: MAT_DATE_LOCALE, useValue: 'pt-BR' }
  ]
})
export class Admin implements OnInit {

  secaoAtiva = 'agendamentos';
  agendamentos: any[] = [];
  agendamentosFiltrados: any[] = [];
  carregando = true;
  dataSelecionada: Date = new Date();

  constructor(
    private agendamentoService: AgendamentoService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) {}

  ngOnInit() {
    this.carregarAgendamentos();
  }

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

  cancelar(id: number) {
    if (!confirm('Deseja cancelar este agendamento?')) return;
    this.agendamentoService.cancelar(id).subscribe({
      next: () => this.carregarAgendamentos(),
      error: (err) => console.error(err)
    });
  }

  // Encerra a sessao e redireciona para o login
  logout() {
    sessionStorage.removeItem('admin');
    this.router.navigate(['/admin/login']);
  }
}
