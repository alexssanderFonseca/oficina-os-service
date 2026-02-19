package br.com.alexsdm.postech.oficina.ordem_servico.core.port.out;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.OrdemServico;

public interface OrdemServicoPagamentoPort {
    void solicitarPagamento(OrdemServico ordemServico);
}
