package com.barbearia.agendamentos.controller;

import com.barbearia.agendamentos.entity.Admin;
import com.barbearia.agendamentos.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/cadastrar")
    public ResponseEntity<Admin> cadastrar(@RequestBody Admin admin) {
        return ResponseEntity.ok(adminService.salvar(admin));
    }

    @GetMapping("/{usuario}")
    public ResponseEntity<Admin> buscar(@PathVariable String usuario) {
        return ResponseEntity.ok(adminService.buscarPorUsuario(usuario));
    }
}