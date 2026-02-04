package br.com.alexsdm.postech.oficina.ordem_servico.core.port.in;

import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.input.FinalizarDiagnosticoInput;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output.FinalizarDiagnosticoOutput;

public interface FinalizarDiagnosticoUseCase {
    FinalizarDiagnosticoOutput executar(FinalizarDiagnosticoInput dto);
}