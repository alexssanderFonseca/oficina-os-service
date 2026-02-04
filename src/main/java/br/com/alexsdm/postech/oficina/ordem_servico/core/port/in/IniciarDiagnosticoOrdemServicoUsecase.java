package br.com.alexsdm.postech.oficina.ordem_servico.core.port.in;

import java.util.UUID;

public interface IniciarDiagnosticoOrdemServicoUsecase {
    void executar(UUID osId);
}
