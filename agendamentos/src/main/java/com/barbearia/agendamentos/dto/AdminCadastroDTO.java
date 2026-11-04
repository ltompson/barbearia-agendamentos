package com.barbearia.agendamentos.dto;

public record AdminCadastroDTO(
        String nome,
        Long barbeiroId,
        String usuario,
        String senha
) {
}