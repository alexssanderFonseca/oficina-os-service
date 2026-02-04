package br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Orcamento {
    private UUID id;
    private UUID clienteId;
    private UUID veiculoId;
    private String status;
    private List<ItemServicoOrcamento> servicos;
    private List<ItemPecaOrcamento> pecas;

}
