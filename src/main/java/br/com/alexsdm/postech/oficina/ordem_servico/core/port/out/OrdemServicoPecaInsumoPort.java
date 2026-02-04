package br.com.alexsdm.postech.oficina.ordem_servico.core.port.out;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.PecaInsumo;

import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoPecaInsumoPort {
    Optional<PecaInsumo> buscarPecaInsumo(UUID id);
}

