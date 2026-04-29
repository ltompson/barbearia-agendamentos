import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

// Guard que protege rotas do painel admin
// Redireciona para login se o usuário não estiver autenticado
export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  const admin = sessionStorage.getItem('admin');

  if (admin) {
    return true;
  }

  router.navigate(['/admin/login']);
  return false;
};
