package br.com.oficina.ordem_servico.adapter.in.controller.request;

import java.util.List;
import java.util.UUID;

public record FinalizarDiagnosticoRequest(List<ItemRequest> itens) {

    public record ItemRequest(UUID id, int quantidade, String tipo) {}
}