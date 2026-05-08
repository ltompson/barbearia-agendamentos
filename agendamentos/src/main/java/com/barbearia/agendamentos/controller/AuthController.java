package com.barbearia.agendamentos.controller;

import com.barbearia.agendamentos.entity.Admin;
import com.barbearia.agendamentos.service.AdminService;
import com.barbearia.agendamentos.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AdminService adminService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String usuario = body.get("usuario");
        String senha = body.get("senha");

        try {
            Admin admin = adminService.buscarPorUsuario(usuario);

            if (!admin.getAtivo()) {
                return ResponseEntity.status(403).body("Acesso suspenso. Entre em contato com o administrador.");
            }

            if (!passwordEncoder.matches(senha, admin.getSenha())) {
                return ResponseEntity.status(401).body("Senha incorreta");
            }

            // Inclui role no token
            String token = jwtService.gerarToken(usuario, admin.getRole(), admin.getBarbeiroId());
            return ResponseEntity.ok(Map.of("token", token, "role", admin.getRole(), "barbeiroId", admin.getBarbeiroId()));

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Usuário não encontrado");
        }
    }
}