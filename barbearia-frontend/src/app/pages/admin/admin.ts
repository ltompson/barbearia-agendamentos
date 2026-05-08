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
import { MatIconModule } from '@angular/material/icon';
import { ServicoService, Servico } from '../../services/servico.service';
import { BarbeiroService, Barbeiro } from '../../services/barbeiro.service';
import { FuncionarioService, Funcionario } from '../../services/funcionario.service';

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
    MatSelectModule,
    MatIconModule,
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
    private router: Router,
    private servicoService: ServicoService,
    private barbeiroService: BarbeiroService,
    private funcionarioService: FuncionarioService
  ) { }

  get temaEscuro(): boolean {
    return this.themeService.darkMode;
  }

  get usuarioLogado(): string {
    return this.authService.getUsuario();
  }

  mesAtual: number = new Date().getMonth();
  anoAtual: number = new Date().getFullYear();
  diasCalendario: (number | null)[] = [];
  nomesMeses = ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'];

  get nomeMes(): string {
    return this.nomesMeses[this.mesAtual];
  }

  gerarCalendario() {
    const primeiroDia = new Date(this.anoAtual, this.mesAtual, 1).getDay();
    const totalDias = new Date(this.anoAtual, this.mesAtual + 1, 0).getDate();
    this.diasCalendario = [];
    for (let i = 0; i < primeiroDia; i++) this.diasCalendario.push(null);
    for (let i = 1; i <= totalDias; i++) this.diasCalendario.push(i);
  }

  mesAnterior() {
    if (this.mesAtual === 0) { this.mesAtual = 11; this.anoAtual--; }
    else this.mesAtual--;
    this.gerarCalendario();
  }

  proximoMes() {
    if (this.mesAtual === 11) { this.mesAtual = 0; this.anoAtual++; }
    else this.mesAtual++;
    this.gerarCalendario();
  }

  isHoje(dia: number): boolean {
    const hoje = new Date();
    return dia === hoje.getDate() && this.mesAtual === hoje.getMonth() && this.anoAtual === hoje.getFullYear();
  }

  isSelecionado(dia: number): boolean {
    if (!this.dataSelecionada) return false;
    const d = new Date(this.dataSelecionada);
    return dia === d.getDate() && this.mesAtual === d.getMonth() && this.anoAtual === d.getFullYear();
  }

  temAgendamento(dia: number): boolean {
    return this.agendamentos.some(ag => {
      const d = new Date(ag.dataHora);
      return d.getDate() === dia && d.getMonth() === this.mesAtual && d.getFullYear() === this.anoAtual
        && ag.status !== 'CANCELADO';
    });
  }

  selecionarDia(dia: number) {
    this.dataSelecionada = new Date(this.anoAtual, this.mesAtual, dia);
    this.filtrarPorData();
  }

  toggleTema() {
    this.themeService.toggle();
  }

  ngOnInit() {
    this.carregarAgendamentos();
    this.gerarCalendario();
    this.carregarServicos();
    this.carregarBarbeiros();
    this.carregarFuncionarios();
  }

  carregarAgendamentos() {
    this.carregando = true;
    const barbeiroId = this.authService.getBarbeiroId();

    const obs = (this.authService.isAdmin() || !barbeiroId)
      ? this.agendamentoService.listar()
      : this.agendamentoService.listarPorBarbeiro(barbeiroId);

    obs.subscribe({
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

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  navegarPara(secao: string): void {
    this.secaoAtiva = secao;
    if (this.isMobile()) {
      this.toggleMenu();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
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

  // Serviços
  servicos: Servico[] = [];
  servicoEditando: Servico | null = null;
  servicoForm = { nome: '', preco: 0, duracaoMinutos: 30 };
  notificacao: string = '';

  carregarServicos() {
    this.servicoService.listarTodos().subscribe({
      next: (dados) => { this.servicos = dados; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
  }

  salvarServico() {
    if (!this.servicoForm.nome || !this.servicoForm.preco) {
      alert('Preencha nome e preço.');
      return;
    }
    const servico: Servico = {
      nome: this.servicoForm.nome,
      preco: this.servicoForm.preco,
      duracaoMinutos: this.servicoForm.duracaoMinutos,
      ativo: true
    };
    if (this.servicoEditando?.id) {
      this.servicoService.atualizar(this.servicoEditando.id, servico).subscribe({
        next: () => {
          this.cancelarEdicao();
          this.carregarServicos();
          this.mostrarNotificacao('✅ Serviço atualizado com sucesso!');
        },
        error: (err) => console.error(err)
      });
    } else {
      this.servicoService.criar(servico).subscribe({
        next: () => {
          this.cancelarEdicao();
          this.carregarServicos();
          this.mostrarNotificacao('✅ Serviço criado com sucesso!');
        },
        error: (err) => console.error(err)
      });
    }
  }

  mostrarNotificacao(msg: string) {
    this.notificacao = msg;
    this.cdr.detectChanges();
    setTimeout(() => {
      this.notificacao = '';
      this.cdr.detectChanges();
    }, 3000);
  }

  editarServico(s: Servico) {
    this.servicoEditando = s;
    this.servicoForm = { nome: s.nome, preco: s.preco, duracaoMinutos: s.duracaoMinutos };
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelarEdicao() {
    this.servicoEditando = null;
    this.servicoForm = { nome: '', preco: 0, duracaoMinutos: 30 };
  }

  toggleServico(s: Servico) {
    const acao = s.ativo ? 'desativar' : 'ativar';
    if (!confirm(`Deseja ${acao} o serviço "${s.nome}"?`)) return;
    this.servicoService.toggleAtivo(s.id!).subscribe({
      next: () => this.carregarServicos(),
      error: (err) => console.error(err)
    });
  }

  reagendar(ag: any) {
    const telefone = ag.cliente?.telefone?.replace(/\D/g, '');
    const nome = ag.cliente?.nome;
    const servico = ag.servico?.nome;
    const data = new Date(ag.dataHora).toLocaleDateString('pt-BR');
    const hora = new Date(ag.dataHora).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
    const linkAgendamento = 'http://localhost:4200';

    const mensagem =
      `Olá ${nome}!\n\n` +
      `Infelizmente não será possível atendê-lo(a) no horário agendado:\n` +
      `Data: *${data}* às *${hora}*\n` +
      `Serviço: *${servico}*\n\n` +
      `Pedimos desculpas pelo transtorno. Para reagendar, acesse:\n` +
      `${linkAgendamento}\n\n` +
      `Alquimista Barbearia`;

    const url = `https://wa.me/55${telefone}?text=${encodeURIComponent(mensagem)}`;
    window.open(url, '_blank');
  }

  // Profissionais
  barbeiros: Barbeiro[] = [];
  barbeiroEditando: Barbeiro | null = null;
  barbeiroForm = { nome: '', telefone: '' };
  notificacaoBarbeiro: string = '';

  carregarBarbeiros() {
    this.barbeiroService.listarTodos().subscribe({
      next: (dados) => { this.barbeiros = dados; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
  }

  salvarBarbeiro() {
    if (!this.barbeiroForm.nome || !this.barbeiroForm.telefone) {
      alert('Preencha nome e telefone.');
      return;
    }
    const barbeiro: Barbeiro = {
      nome: this.barbeiroForm.nome,
      telefone: this.barbeiroForm.telefone,
      ativo: true
    };
    if (this.barbeiroEditando?.id) {
      this.barbeiroService.atualizar(this.barbeiroEditando.id, barbeiro).subscribe({
        next: () => {
          this.cancelarEdicaoBarbeiro();
          this.carregarBarbeiros();
          this.notificacaoBarbeiro = '✅ Profissional atualizado com sucesso!';
          setTimeout(() => { this.notificacaoBarbeiro = ''; this.cdr.detectChanges(); }, 3000);
        },
        error: (err) => console.error(err)
      });
    } else {
      this.barbeiroService.criar(barbeiro).subscribe({
        next: () => {
          this.cancelarEdicaoBarbeiro();
          this.carregarBarbeiros();
          this.notificacaoBarbeiro = '✅ Profissional adicionado com sucesso!';
          setTimeout(() => { this.notificacaoBarbeiro = ''; this.cdr.detectChanges(); }, 3000);
        },
        error: (err) => console.error(err)
      });
    }
  }

  editarBarbeiro(b: Barbeiro) {
    this.barbeiroEditando = b;
    this.barbeiroForm = { nome: b.nome, telefone: b.telefone };
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelarEdicaoBarbeiro() {
    this.barbeiroEditando = null;
    this.barbeiroForm = { nome: '', telefone: '' };
  }

  toggleBarbeiro(b: Barbeiro) {
    const acao = b.ativo ? 'desativar' : 'ativar';
    if (!confirm(`Deseja ${acao} o profissional "${b.nome}"?`)) return;
    this.barbeiroService.toggleAtivo(b.id!).subscribe({
      next: () => this.carregarBarbeiros(),
      error: (err) => console.error(err)
    });
  }

  // Funcionários (acesso ao painel)
  funcionarios: Funcionario[] = [];
  funcionarioEditando: Funcionario | null = null;
  funcionarioForm = { nome: '', usuario: '', senha: '' };
  notificacaoFuncionario: string = '';

  carregarFuncionarios() {
    this.funcionarioService.listarTodos().subscribe({
      next: (dados) => { this.funcionarios = dados; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
  }

  salvarFuncionario() {
    if (!this.funcionarioForm.nome || !this.funcionarioForm.usuario) {
      alert('Preencha nome e usuário.');
      return;
    }
    if (!this.funcionarioEditando && !this.funcionarioForm.senha) {
      alert('Preencha a senha.');
      return;
    }
    const funcionario: Funcionario = {
      nome: this.funcionarioForm.nome,
      usuario: this.funcionarioForm.usuario,
      senha: this.funcionarioForm.senha || undefined,
      ativo: true
    };
    if (this.funcionarioEditando?.id) {
      this.funcionarioService.atualizar(this.funcionarioEditando.id, funcionario).subscribe({
        next: () => {
          this.cancelarEdicaoFuncionario();
          this.carregarFuncionarios();
          this.notificacaoFuncionario = '✅ Funcionário atualizado com sucesso!';
          setTimeout(() => { this.notificacaoFuncionario = ''; this.cdr.detectChanges(); }, 3000);
        },
        error: (err) => console.error(err)
      });
    } else {
      this.funcionarioService.criar(funcionario).subscribe({
        next: () => {
          this.cancelarEdicaoFuncionario();
          this.carregarFuncionarios();
          this.notificacaoFuncionario = '✅ Funcionário cadastrado com sucesso!';
          setTimeout(() => { this.notificacaoFuncionario = ''; this.cdr.detectChanges(); }, 3000);
        },
        error: (err) => console.error(err)
      });
    }
  }

  editarFuncionario(f: Funcionario) {
    this.funcionarioEditando = f;
    this.funcionarioForm = { nome: f.nome, usuario: f.usuario, senha: '' };
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelarEdicaoFuncionario() {
    this.funcionarioEditando = null;
    this.funcionarioForm = { nome: '', usuario: '', senha: '' };
  }

  toggleFuncionario(f: Funcionario) {
    const acao = f.ativo ? 'pausar' : 'reativar';
    if (!confirm(`Deseja ${acao} o acesso de "${f.nome}"?`)) return;
    this.funcionarioService.toggleAtivo(f.id!).subscribe({
      next: () => this.carregarFuncionarios(),
      error: (err) => console.error(err)
    });
  }

  deletarFuncionario(f: Funcionario) {
    if (!confirm(`Deseja remover permanentemente "${f.nome}"?`)) return;
    this.funcionarioService.deletar(f.id!).subscribe({
      next: () => this.carregarFuncionarios(),
      error: (err) => console.error(err)
    });
  }
  // Encerra a sessao e redireciona para o login
  logout() {
    this.authService.logout();
    this.router.navigate(['/admin/login']);
  }
}
