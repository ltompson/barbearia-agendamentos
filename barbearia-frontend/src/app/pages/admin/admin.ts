import { BloqueioService, Bloqueio } from '../../services/bloqueio';
import { MatSelectModule } from '@angular/material/select';
import { Component, OnInit, ChangeDetectorRef, LOCALE_ID, inject } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, MAT_DATE_LOCALE } from '@angular/material/core';
import { AgendamentoService } from '../../services/agendamento';
import localePt from '@angular/common/locales/pt';
import { Router } from '@angular/router';
import { ThemeService } from '../../services/theme.service';
import { AuthService } from '../../services/auth.service';
import { DiaDisponivelService } from '../../services/dia-disponivel.service';

registerLocaleData(localePt);

@Component({
  selector: 'app-admin',
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSelectModule
  ],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
  providers: [
    { provide: LOCALE_ID, useValue: 'pt-BR' },
    { provide: MAT_DATE_LOCALE, useValue: 'pt-BR' }
  ]
})
export class Admin implements OnInit {

  private themeService = inject(ThemeService);

  secaoAtiva = 'agendamentos';
  agendamentos: any[] = [];
  agendamentosFiltrados: any[] = [];
  carregando = true;
  dataSelecionada: Date = new Date();

  constructor(
    private agendamentoService: AgendamentoService,
    private bloqueioService: BloqueioService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private diaDisponivelService: DiaDisponivelService,
    private router: Router
  ) { }

  get temaEscuro(): boolean {
    return this.themeService.darkMode;
  }

  toggleTema() {
    this.themeService.toggle();
  }

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

  // Bloqueios
  bloqueios: any[] = [];
  bloqueioForm = {
    barbeiroId: null as number | null,
    data: null as Date | null,
    horario: '',
    motivo: ''
  };
  horariosBloqueio = [
    '08:00', '08:30', '09:00', '09:30', '10:00', '10:30',
    '11:00', '11:30', '12:00', '12:30', '13:00', '13:30',
    '14:00', '14:30', '15:00', '15:30', '16:00', '16:30',
    '17:00', '17:30'
  ];

  cancelar(id: number) {
    if (!confirm('Deseja cancelar este agendamento?')) return;
    this.agendamentoService.cancelar(id).subscribe({
      next: () => this.carregarAgendamentos(),
      error: (err) => console.error(err)
    });
  }

  carregarBloqueios() {
    if (!this.bloqueioForm.data) return;
    const data = this.formatarData(this.bloqueioForm.data);
    this.bloqueioService.listarPorData(data).subscribe({
      next: (dados) => { this.bloqueios = dados; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
  }

  criarBloqueio() {
    const { barbeiroId, data, horario, motivo } = this.bloqueioForm;
    if (!barbeiroId || !data || !horario) {
      alert('Preencha barbeiro, data e horário.');
      return;
    }
    const bloqueio: Bloqueio = {
      barbeiroId,
      data: this.formatarData(data),
      horario: horario + ':00',
      motivo
    };
    this.bloqueioService.criar(bloqueio).subscribe({
      next: () => {
        this.bloqueioForm.horario = '';
        this.bloqueioForm.motivo = '';
        this.carregarBloqueios();
      },
      error: (err) => console.error(err)
    });
  }

  deletarBloqueio(id: number) {
    if (!confirm('Remover este bloqueio?')) return;
    this.bloqueioService.deletar(id).subscribe({
      next: () => this.carregarBloqueios(),
      error: (err) => console.error(err)
    });
  }

  private formatarData(data: Date): string {
    const d = new Date(data);
    const ano = d.getFullYear();
    const mes = String(d.getMonth() + 1).padStart(2, '0');
    const dia = String(d.getDate()).padStart(2, '0');
    return `${ano}-${mes}-${dia}`;
  }

  menuAberto = false;
  menuFechando = false;

  toggleMenu() {
    if (this.menuAberto) {
      this.menuFechando = true;
      this.menuAberto = false;
      this.cdr.detectChanges();
      setTimeout(() => {
        this.menuFechando = false;
        this.cdr.detectChanges();
      }, 180);
    } else {
      this.menuAberto = true;
      this.menuFechando = false;
    }
  }
  isMobile(): boolean {
    return window.innerWidth <= 768;
  }

  carregarDiasDisponiveis() {
    if (!this.diaDisponivelForm.data) return;
    const data = this.formatarData(this.diaDisponivelForm.data);
    this.diaDisponivelService.listarPorData(data).subscribe({
      next: (dados) => { this.diasDisponiveis = dados; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
  }

  diasDisponiveis: any[] = [];
  diaDisponivelForm = {
    barbeiroId: null as number | null,
    data: null as Date | null,
    motivo: '',
    horario: null as string | null
  };

  filtroFimDeSemana = (data: Date | null): boolean => {
    if (!data) return false;
    const dia = data.getDay();
    return dia === 0 || dia === 6;
  };

  criarDiaDisponivel() {
    const { barbeiroId, data, motivo, horario } = this.diaDisponivelForm;
    if (!barbeiroId || !data) {
      alert('Preencha barbeiro e data.');
      return;
    }
    this.diaDisponivelService.criar({
      barbeiroId,
      data: this.formatarData(data),
      motivo,
      horario: horario ?? undefined
    }).subscribe({
      next: () => {
        this.diaDisponivelForm.motivo = '';
        this.diaDisponivelForm.horario = null;
        this.carregarDiasDisponiveis();
      },
      error: (err) => console.error(err)
    });
  }

  deletarDiaDisponivel(id: number) {
    if (!confirm('Remover este dia liberado?')) return;
    this.diaDisponivelService.deletar(id).subscribe({
      next: () => this.carregarDiasDisponiveis(),
      error: (err) => console.error(err)
    });
  }
  // Encerra a sessao e redireciona para o login
  logout() {
    this.authService.logout();
    this.router.navigate(['/admin/login']);
  }
}
