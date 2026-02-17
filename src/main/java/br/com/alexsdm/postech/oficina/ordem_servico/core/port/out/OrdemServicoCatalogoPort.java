package br.com.alexsdm.postech.oficina.ordem_servico.core.port.out;

import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.dto.ItemCatalogoDto;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.dto.ItemParaBaixaEstoque;

import java.util.List;
import java.util.UUID;

public interface OrdemServicoCatalogoPort {
    ItemCatalogoDto buscarItemCatalogo(UUID id, String tipoItem);
    void darBaixaEstoque(List<ItemParaBaixaEstoque> itens);
}
