package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto;

import java.util.UUID;

public record ClienteRequest(
    UUID id,
    String nomeCompleto,
    String cpfCnpj
) {
}
