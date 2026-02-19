package br.com.oficina.ordem_servico.core.port.out;

import br.com.oficina.ordem_servico.core.domain.entity.OrdemServico;

import java.util.UUID;

public interface OrdemServicoOrcamentoPort {

    UUID criar(OrdemServico ordemServico);
}
