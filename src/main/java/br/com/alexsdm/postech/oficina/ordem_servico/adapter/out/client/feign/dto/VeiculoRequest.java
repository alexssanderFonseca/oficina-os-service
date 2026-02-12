package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto;

import java.util.UUID;

public record VeiculoRequest(
    UUID id,
    String marca,
    String modelo,
    String placa
) {
}
