package br.com.oficina.ordem_servico.core.port.in;

import br.com.oficina.ordem_servico.core.usecase.input.FinalizarDiagnosticoInput;
import br.com.oficina.ordem_servico.core.usecase.output.FinalizarDiagnosticoOutput;

public interface FinalizarDiagnosticoUseCase {
    FinalizarDiagnosticoOutput executar(FinalizarDiagnosticoInput dto);
}