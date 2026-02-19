package br.com.oficina.ordem_servico.core.port.out;

import br.com.oficina.ordem_servico.core.domain.entity.Servico;

import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoServicoPort {
   Optional<Servico> buscarServicoPorId(UUID id);
}
