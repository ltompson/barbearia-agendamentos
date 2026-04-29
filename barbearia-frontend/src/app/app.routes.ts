import { Routes } from '@angular/router';
import { Agendamento } from './pages/agendamento/agendamento';
import { Login } from './pages/login/login';
import { Admin } from './pages/admin/admin';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
  { path: '', component: Agendamento },
  { path: 'admin/login', component: Login },
  { path: 'admin/painel', component: Admin, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];
