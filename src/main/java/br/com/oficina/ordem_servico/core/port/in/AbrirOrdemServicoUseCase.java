package br.com.oficina.ordem_servico.core.port.in;

import br.com.oficina.ordem_servico.core.usecase.input.CriarOrdemServicoInput;

import java.util.UUID;

public interface AbrirOrdemServicoUseCase {
    UUID executar(CriarOrdemServicoInput input);
}
