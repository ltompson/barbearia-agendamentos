package com.barbearia.agendamentos.controller;

import com.barbearia.agendamentos.entity.Barbeiro;
import com.barbearia.agendamentos.service.BarbeiroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/barbeiros")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BarbeiroController {

    private final BarbeiroService barbeiroService;

    // Apenas ativos — tela de agendamento
    @GetMapping
    public ResponseEntity<List<Barbeiro>> listar() {
        return ResponseEntity.ok(barbeiroService.listarAtivos());
    }

    // Todos — painel admin
    @GetMapping("/todos")
    public ResponseEntity<List<Barbeiro>> listarTodos() {
        return ResponseEntity.ok(barbeiroService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Barbeiro> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(barbeiroService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Barbeiro> salvar(@RequestBody Barbeiro barbeiro) {
        return ResponseEntity.ok(barbeiroService.salvar(barbeiro));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Barbeiro> atualizar(
            @PathVariable Long id,
            @RequestBody Barbeiro barbeiro) {
        return ResponseEntity.ok(barbeiroService.atualizar(id, barbeiro));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Barbeiro> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(barbeiroService.toggleAtivo(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        barbeiroService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
