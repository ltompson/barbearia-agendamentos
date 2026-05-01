import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  form: FormGroup;
  erro = '';
  carregando = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    this.form = this.fb.group({
      usuario: ['', Validators.required],
      senha: ['', Validators.required]
    });
  }

  entrar() {
    if (this.form.invalid) return;

    this.carregando = true;
    this.erro = '';

    const { usuario, senha } = this.form.value;

    this.authService.login(usuario, senha).subscribe({
      next: () => {
        this.router.navigate(['/admin/painel']);
      },
      error: () => {
        this.erro = 'Usuário ou senha incorretos.';
        this.carregando = false;
      }
    });
  }
}
