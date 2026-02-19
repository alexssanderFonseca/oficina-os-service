package br.com.oficina.ordem_servico.core.port.in;

import java.util.UUID;

public interface EntregarOrdemServicoUseCase {
    void executar(UUID id);
}