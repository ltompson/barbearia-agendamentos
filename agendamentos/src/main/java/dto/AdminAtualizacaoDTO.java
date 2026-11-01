package com.barbearia.agendamentos.dto;

public record AdminAtualizacaoDTO(
        String nome,
        Long barbeiroId,
        String usuario,
        String senha
) {
}