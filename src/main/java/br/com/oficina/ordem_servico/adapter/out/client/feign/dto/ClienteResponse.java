package br.com.oficina.ordem_servico.adapter.out.client.feign.dto;

import java.util.List;
import java.util.UUID;

// Based on the 'Cliente' domain entity and old adapter logic
public record ClienteResponse(
    UUID id,
    String nome,
    String sobrenome,
    String cpfCnpj,
    List<VeiculoResponse> veiculos
) {
}
