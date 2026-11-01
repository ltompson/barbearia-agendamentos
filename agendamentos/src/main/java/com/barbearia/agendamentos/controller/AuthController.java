
package com.barbearia.agendamentos.controller;

import com.barbearia.agendamentos.entity.Admin;
import com.barbearia.agendamentos.service.AdminService;
import com.barbearia.agendamentos.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AdminService adminService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private static final Set<String> ROLES_PERMITIDAS =
            Set.of("ADMIN", "FUNCIONARIO");

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody(required = false) Map<String, String> body) {

        if (body == null) {
            return ResponseEntity.badRequest().body("Dados de login obrigatorios.");
        }

        String usuario = body.get("usuario");
        String senha = body.get("senha");

        if (usuario == null || usuario.isBlank()
                || senha == null || senha.isBlank()) {
            return ResponseEntity.badRequest().body("Usuario e senha obrigatorios.");
        }

        Admin admin;

        try {
            admin = adminService.buscarPorUsuario(usuario.trim());
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Usuario ou senha incorretos.");
        }

        if (admin == null) {
            return ResponseEntity.status(401).body("Usuario ou senha incorretos.");
        }

        if (!Boolean.TRUE.equals(admin.getAtivo())) {
            return ResponseEntity.status(403)
                    .body("Acesso suspenso. Entre em contato com o administrador.");
        }

        if (admin.getSenha() == null
                || !passwordEncoder.matches(senha, admin.getSenha())) {
            return ResponseEntity.status(401).body("Usuario ou senha incorretos.");
        }

        String role = admin.getRole();

        if (role == null || !ROLES_PERMITIDAS.contains(role.trim().toUpperCase())) {
            return ResponseEntity.status(403).body("Perfil de acesso invalido.");
        }

        String token = jwtService.gerarToken(
                admin.getUsuario(),
                role.trim().toUpperCase(),
                admin.getBarbeiroId()
        );

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("token", token);
        resposta.put("role", role.trim().toUpperCase());
        resposta.put("barbeiroId", admin.getBarbeiroId());

        return ResponseEntity.ok(resposta);
    }
}
