package com.barbearia.agendamentos.controller;

import com.barbearia.agendamentos.entity.Admin;
import com.barbearia.agendamentos.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    // Cadastrar novo funcionário (protegido — só admin logado)
    @PostMapping("/cadastrar")
    public ResponseEntity<Admin> cadastrar(@RequestBody Admin admin) {
        return ResponseEntity.ok(adminService.salvar(admin));
    }

    // Listar todos os funcionários (protegido)
    @GetMapping
    public ResponseEntity<List<Admin>> listarTodos() {
        return ResponseEntity.ok(adminService.listarTodos());
    }

    // Buscar por usuário
    @GetMapping("/{usuario}")
    public ResponseEntity<Admin> buscar(@PathVariable String usuario) {
        return ResponseEntity.ok(adminService.buscarPorUsuario(usuario));
    }

    // Atualizar nome/usuario/senha
    @PutMapping("/{id}")
    public ResponseEntity<Admin> atualizar(
            @PathVariable Long id,
            @RequestBody Admin admin) {
        return ResponseEntity.ok(adminService.atualizar(id, admin));
    }

    // Pausar/reativar acesso
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Admin> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleAtivo(id));
    }

    // Remover funcionário
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        adminService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}