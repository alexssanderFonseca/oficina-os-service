package br.com.alexsdm.postech.oficina.ordem_servico.core.port.in;

import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.input.CriarOrdemServicoInput;

import java.util.UUID;

public interface AbrirOrdemServicoUseCase {
    UUID executar(CriarOrdemServicoInput input);
}
