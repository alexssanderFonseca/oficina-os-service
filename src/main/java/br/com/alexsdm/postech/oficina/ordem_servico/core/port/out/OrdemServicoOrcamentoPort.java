package br.com.alexsdm.postech.oficina.ordem_servico.core.port.out;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Orcamento;

import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoOrcamentoPort {
    Optional<Orcamento> buscarPorId(UUID id);
    UUID criar(Orcamento orcamento);
}
