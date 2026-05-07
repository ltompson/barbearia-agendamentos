package com.barbearia.agendamentos.controller;

import com.barbearia.agendamentos.entity.Servico;
import com.barbearia.agendamentos.service.ServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/servicos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServicoController {

    private final ServicoService servicoService;

    // Apenas ativos — tela de agendamento
    @GetMapping
    public ResponseEntity<List<Servico>> listar() {
        return ResponseEntity.ok(servicoService.listarAtivos());
    }

    // Todos — painel admin
    @GetMapping("/todos")
    public ResponseEntity<List<Servico>> listarTodos() {
        return ResponseEntity.ok(servicoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servico> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Servico> salvar(@RequestBody Servico servico) {
        return ResponseEntity.ok(servicoService.salvar(servico));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Servico> atualizar(
            @PathVariable Long id,
            @RequestBody Servico servico) {
        return ResponseEntity.ok(servicoService.atualizar(id, servico));
    }

    // Liga/desliga ativo
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Servico> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(servicoService.toggleAtivo(id));
    }

    // Deleta serviço
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        servicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}