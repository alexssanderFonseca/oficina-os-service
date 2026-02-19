package br.com.oficina.ordem_servico.adapter.out.client.feign.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CriarOrcamentoRequest(
        @NotNull @Valid ClienteRequest cliente,
        @NotNull @Valid VeiculoRequest veiculo,
        @Valid List<ItemOrcamentoRequest> itens,
        @NotNull BigDecimal valorTotal,
        @NotNull UUID ordemServicoId
) {
}
