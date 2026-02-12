package br.com.alexsdm.postech.oficina.ordem_servico.core.port.out;

import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.dto.ItemCatalogoDto;

import java.util.UUID;

public interface OrdemServicoCatalogoPort {
    ItemCatalogoDto buscarItemCatalogo(UUID id, String tipoItem);
}

