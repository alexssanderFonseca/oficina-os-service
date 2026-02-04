package br.com.alexsdm.postech.oficina.ordem_servico.core.port.in;

import br.com.alexsdm.postech.oficina.ordem_servico.core.pagination.Page;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output.ListarOrdemServicoUsecaseOutput;

public interface ListarOrdemServicoUsecase {
    Page<ListarOrdemServicoUsecaseOutput> executar(Long pagina, Long quantidade);
}
