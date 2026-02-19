package br.com.oficina.ordem_servico.core.port.in;

import br.com.oficina.ordem_servico.core.pagination.Page;
import br.com.oficina.ordem_servico.core.usecase.output.ListarOrdemServicoUsecaseOutput;

public interface ListarOrdemServicoUsecase {
    Page<ListarOrdemServicoUsecaseOutput> executar(Long pagina, Long quantidade);
}
