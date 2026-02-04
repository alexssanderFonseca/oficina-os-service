package br.com.alexsdm.postech.oficina.ordem_servico.core.port.out;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Servico;

import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoServicoPort {
   Optional<Servico> buscarServicoPorId(UUID id);
}
