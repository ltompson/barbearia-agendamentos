import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private isDark = false;

  constructor() {
    const saved = localStorage.getItem('tema');
    this.isDark = saved === 'dark';
    this.applyTheme();
  }

  toggle() {
    this.isDark = !this.isDark;
    localStorage.setItem('tema', this.isDark ? 'dark' : 'light');
    this.applyTheme();
  }

  get darkMode(): boolean {
    return this.isDark;
  }

  private applyTheme() {
    document.body.classList.toggle('dark-theme', this.isDark);
  }
}
