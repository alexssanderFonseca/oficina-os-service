package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto;

import java.math.BigDecimal;
import java.util.List;

public record CriarOrcamentoRequest(
        ClienteRequest cliente,
        VeiculoRequest veiculo,
        List<ItemOrcamentoRequest> itens,
        BigDecimal valorTotal
) {
}
