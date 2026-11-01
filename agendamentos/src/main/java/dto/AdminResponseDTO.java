package com.barbearia.agendamentos.dto;

import com.barbearia.agendamentos.entity.Admin;

import java.time.LocalDateTime;

public record AdminResponseDTO(
        Long id,
        String nome,
        Long barbeiroId,
        String usuario,
        Boolean ativo,
        String role,
        LocalDateTime criadoEm
) {

    public static AdminResponseDTO fromEntity(Admin admin) {
        return new AdminResponseDTO(
                admin.getId(),
                admin.getNome(),
                admin.getBarbeiroId(),
                admin.getUsuario(),
                admin.getAtivo(),
                admin.getRole(),
                admin.getCriadoEm()
        );
    }
}