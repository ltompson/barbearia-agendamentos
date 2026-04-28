package com.barbearia.agendamentos.service;

import com.barbearia.agendamentos.entity.Admin;
import com.barbearia.agendamentos.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public Admin salvar(Admin admin) {
        admin.setSenha(passwordEncoder.encode(admin.getSenha()));
        return adminRepository.save(admin);
    }

    public Admin buscarPorUsuario(String usuario) {
        return adminRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Admin não encontrado"));
    }
}