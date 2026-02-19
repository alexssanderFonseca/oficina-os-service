package br.com.oficina.ordem_servico.core.port.out;

import br.com.oficina.ordem_servico.core.domain.entity.OrdemServico;

public interface OrdemServicoPagamentoPort {
    void solicitarPagamento(OrdemServico ordemServico);
}
