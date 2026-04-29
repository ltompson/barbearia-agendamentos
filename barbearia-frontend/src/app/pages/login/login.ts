import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-login',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  form: FormGroup;
  erro = '';

  // Credenciais válidas (temporário — depois vamos conectar ao backend)
  private usuarios = [
    { usuario: 'francisco', senha: '1234' },
    { usuario: 'raniel', senha: '1234' }
  ];

  constructor(private fb: FormBuilder, private router: Router) {
    this.form = this.fb.group({
      usuario: ['', Validators.required],
      senha:   ['', Validators.required]
    });
  }

  // Valida as credenciais e redireciona para o painel
  entrar() {
    if (this.form.invalid) return;

    const { usuario, senha } = this.form.value;

    const encontrado = this.usuarios.find(
      u => u.usuario === usuario && u.senha === senha
    );

    if (encontrado) {
      // Salva no sessionStorage que o usuário está logado
      sessionStorage.setItem('admin', usuario);
      this.router.navigate(['/admin/painel']);
    } else {
      this.erro = 'Usuário ou senha incorretos.';
    }
  }
}
