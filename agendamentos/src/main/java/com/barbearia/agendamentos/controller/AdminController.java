package com.barbearia.agendamentos.controller;

import com.barbearia.agendamentos.dto.AdminAtualizacaoDTO;
import com.barbearia.agendamentos.dto.AdminCadastroDTO;
import com.barbearia.agendamentos.dto.AdminResponseDTO;
import com.barbearia.agendamentos.entity.Admin;
import com.barbearia.agendamentos.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/cadastrar")
    public ResponseEntity<AdminResponseDTO> cadastrar(
            @RequestBody AdminCadastroDTO dados) {

        Admin admin = new Admin();
        admin.setNome(dados.nome());
        admin.setBarbeiroId(dados.barbeiroId());
        admin.setUsuario(dados.usuario());
        admin.setSenha(dados.senha());

        Admin salvo = adminService.salvar(admin);

        return ResponseEntity.ok(AdminResponseDTO.fromEntity(salvo));
    }

    @GetMapping
    public ResponseEntity<List<AdminResponseDTO>> listarTodos() {

        List<AdminResponseDTO> resposta = adminService.listarTodos()
                .stream()
                .map(AdminResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{usuario}")
    public ResponseEntity<AdminResponseDTO> buscar(
            @PathVariable String usuario) {

        Admin admin = adminService.buscarPorUsuario(usuario);

        return ResponseEntity.ok(AdminResponseDTO.fromEntity(admin));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody AdminAtualizacaoDTO dados) {

        Admin admin = new Admin();
        admin.setNome(dados.nome());
        admin.setBarbeiroId(dados.barbeiroId());
        admin.setUsuario(dados.usuario());
        admin.setSenha(dados.senha());

        Admin atualizado = adminService.atualizar(id, admin);

        return ResponseEntity.ok(AdminResponseDTO.fromEntity(atualizado));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<AdminResponseDTO> toggle(@PathVariable Long id) {

        Admin admin = adminService.toggleAtivo(id);

        return ResponseEntity.ok(AdminResponseDTO.fromEntity(admin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        adminService.deletar(id);

        return ResponseEntity.noContent().build();
    }
}