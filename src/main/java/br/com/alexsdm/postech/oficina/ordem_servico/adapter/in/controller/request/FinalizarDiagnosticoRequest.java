package br.com.alexsdm.postech.oficina.ordem_servico.adapter.in.controller.request;

import java.util.List;
import java.util.UUID;

public record FinalizarDiagnosticoRequest(List<ItemPeca> idPecasNecessarias,
                                          List<UUID> idServicosNecessarios) {

    public record ItemPeca(UUID idPeca, int qtd) {}
}