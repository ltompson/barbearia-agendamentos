import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AgendamentoService } from '../../services/agendamento';
import { MAT_DATE_LOCALE } from '@angular/material/core';

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
    MatSnackBarModule
  ],
  templateUrl: './agendamento.html',
  styleUrl: './agendamento.css',
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'pt-BR' }
  ]
})
export class Agendamento {

  // Formulário reativo com validações
  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private agendamentoService: AgendamentoService,
    private snackBar: MatSnackBar
  ) {
    // Define os campos e suas validações
    this.form = this.fb.group({
      nomeCliente: ['', Validators.required],
      telefoneCliente: ['', Validators.required],
      barbeiroId: ['', Validators.required],
      servicoId: ['', Validators.required],
      data: ['', Validators.required],
      horario: ['', Validators.required]
    });
  }

  // Executado ao clicar em "Confirmar Agendamento"
  confirmar() {

    // Se algum campo estiver vazio, não envia
    if (this.form.invalid) {
      this.snackBar.open('Preencha todos os campos!', 'Fechar', { duration: 3000 });
      return;
    }

    const { nomeCliente, telefoneCliente, barbeiroId, servicoId, data, horario } = this.form.value;

    // Combina a data e o horário no formato que o backend espera
    const dataHora = `${this.formatarData(data)}T${horario}:00`;

    const payload = { nomeCliente, telefoneCliente, barbeiroId, servicoId, dataHora };

    // Envia para o backend
    this.agendamentoService.criar(payload).subscribe({
      next: () => {
        this.snackBar.open('Agendamento realizado com sucesso! ✅', 'Fechar', { duration: 4000 });
        this.form.reset();
      },
      error: (err) => {
        console.error(err);
        this.snackBar.open('Erro ao agendar. Tente novamente.', 'Fechar', { duration: 4000 });
      }
    });
  }

  // Formata a data para o padrão YYYY-MM-DD
  private formatarData(data: Date): string {
    const d = new Date(data);
    const ano = d.getFullYear();
    const mes = String(d.getMonth() + 1).padStart(2, '0');
    const dia = String(d.getDate()).padStart(2, '0');
    return `${ano}-${mes}-${dia}`;
  }
}
