package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto;

import java.util.UUID;

// Based on the 'Veiculo' domain entity and old adapter logic
public record VeiculoResponse(
    UUID id,
    String placa,
    String marca,
    String modelo,
    String ano,
    String cor
) {
}
