package br.com.oficina.ordem_servico.adapter.out.client.feign.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ClienteRequest(
    @NotNull UUID id,
    @NotNull String nome,
    @NotNull String documento
) {
}
