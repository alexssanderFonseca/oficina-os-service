package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record VeiculoRequest(
    @NotNull UUID id,
    @NotNull String marca,
    @NotNull String modelo,
    @NotNull String placa
) {
}
