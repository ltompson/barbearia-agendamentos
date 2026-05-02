import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AgendamentoService } from '../../services/agendamento';
import { ThemeService } from '../../services/theme.service';
import { CommonModule } from '@angular/common';
import { DiaDisponivelService } from '../../services/dia-disponivel.service';

@Component({
  selector: 'app-agendamento',
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatSnackBarModule,
    CommonModule,
  ],
  templateUrl: './agendamento.html',
  styleUrl: './agendamento.css',
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'pt-BR' }
  ]
})
export class Agendamento {

  form: FormGroup;
  horariosDisponiveis: string[] = [];
  diasExcecao: Set<string> = new Set();
  filtroDataFn: (data: Date | null) => boolean = () => true;
  private themeService = inject(ThemeService);

  constructor(
    private fb: FormBuilder,
    private agendamentoService: AgendamentoService,
    private diaDisponivelService: DiaDisponivelService,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef
  ) {
    this.atualizarFiltroData();

    this.form = this.fb.group({
      nomeCliente: ['', Validators.required],
      telefoneCliente: ['', Validators.required],
      barbeiroId: ['', Validators.required],
      servicoId: ['', Validators.required],
      data: ['', Validators.required],
      horario: ['', Validators.required]
    });

    this.form.get('data')?.valueChanges.subscribe(() => this.onDataOuBarbeiroChange());
    this.form.get('barbeiroId')?.valueChanges.subscribe((barbeiroId) => {
      if (barbeiroId) {
        this.diasExcecao.clear();
        this.diaDisponivelService.listarPorBarbeiro(barbeiroId).subscribe({
          next: (dias) => {
            dias.forEach(d => this.diasExcecao.add(d.data));
            this.atualizarFiltroData();
            this.cdr.detectChanges();
          }
        });
      }
      this.onDataOuBarbeiroChange();
    });
  }

  get temaEscuro(): boolean {
    return this.themeService.darkMode;
  }

  toggleTema() {
    this.themeService.toggle();
  }

  private atualizarFiltroData() {
    this.filtroDataFn = (data: Date | null): boolean => {
      if (!data) return false;
      const dia = data.getDay();
      const dataISO = this.formatarData(data);
      if (dia === 0 || dia === 6) {
        return this.diasExcecao.has(dataISO);
      }
      return true;
    };
  }

  onDataOuBarbeiroChange() {
    const { data, barbeiroId } = this.form.value;
    if (!barbeiroId || !data) return;

    const dataISO = this.formatarData(data);
    const diaDaSemana = new Date(data).getDay();

    if (diaDaSemana === 0 || diaDaSemana === 6) {
      // Verifica no backend se esse dia específico está liberado
      this.diaDisponivelService.isDiaDisponivel(dataISO, barbeiroId).subscribe({
        next: (disponivel) => {
          if (disponivel) {
            this.buscarHorarios(data, barbeiroId);
          } else {
            this.horariosDisponiveis = [];
          }
        }
      });
    } else {
      this.buscarHorarios(data, barbeiroId);
    }
  }

  private buscarHorarios(data: Date, barbeiroId: number) {
    this.agendamentoService.getHorariosDisponiveis(data, barbeiroId).subscribe({
      next: (horarios) => {
        this.horariosDisponiveis = horarios;
        this.form.patchValue({ horario: '' });
      },
      error: () => {
        this.snackBar.open('Erro ao buscar horários.', 'Fechar', { duration: 3000 });
      }
    });
  }

  horariosManha(): string[] {
    return this.horariosDisponiveis.filter(h => parseInt(h.split(':')[0]) < 12);
  }

  horariosTarde(): string[] {
    return this.horariosDisponiveis.filter(h => {
      const hora = parseInt(h.split(':')[0]);
      return hora >= 12 && hora < 18;
    });
  }

  horariosNoite(): string[] {
    return this.horariosDisponiveis.filter(h => parseInt(h.split(':')[0]) >= 18);
  }

  confirmar() {
    if (this.form.invalid) {
      this.snackBar.open('Preencha todos os campos!', 'Fechar', { duration: 3000 });
      return;
    }

    const { nomeCliente, telefoneCliente, barbeiroId, servicoId, data, horario } = this.form.value;
    const dataHora = `${this.formatarData(data)}T${horario.substring(0, 5)}:00`;
    const payload = { nomeCliente, telefoneCliente, barbeiroId, servicoId, dataHora };

    this.agendamentoService.criar(payload).subscribe({
      next: () => {
        this.snackBar.open('Agendamento realizado com sucesso! ✅', 'Fechar', { duration: 4000 });
        this.form.reset();
        this.horariosDisponiveis = [];
      },
      error: (err) => {
        console.error(err);
        this.snackBar.open('Erro ao agendar. Tente novamente.', 'Fechar', { duration: 4000 });
      }
    });
  }

  private formatarData(data: Date): string {
    const d = new Date(data);
    const ano = d.getFullYear();
    const mes = String(d.getMonth() + 1).padStart(2, '0');
    const dia = String(d.getDate()).padStart(2, '0');
    return `${ano}-${mes}-${dia}`;
  }
}
