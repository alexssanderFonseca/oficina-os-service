package br.com.alexsdm.postech.oficina.ordem_servico.core.port.out;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.OrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.pagination.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoRepository {
    OrdemServico salvar(OrdemServico ordemServico);

    Optional<OrdemServico> buscarPorId(UUID id);

    List<OrdemServico> buscarPeloIdCliente(UUID clienteId);

    List<OrdemServico> buscarFinalizadas();


    Page<OrdemServico> listarTodasOrdenadas(Long pagina, Long quantidade);
}
