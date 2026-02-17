package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.OrcamentoFeignClient;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.ClienteRequest;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.CriarOrcamentoRequest;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.ItemOrcamentoRequest;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.VeiculoRequest;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.CriarOrcamentoResponse; // New Import
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.OrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoOrcamentoPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrdemServicoOrcamentoAdapter implements OrdemServicoOrcamentoPort {

    private static final Logger logger = LoggerFactory.getLogger(OrdemServicoOrcamentoAdapter.class);
    private final OrcamentoFeignClient orcamentoFeignClient;

    @Override
    public UUID criar(OrdemServico ordemServico) {
        logger.info("Tentando criar orçamento para Ordem de Serviço ID: {}", ordemServico.getId());
        var itens = ordemServico.getItens().stream()
                .map(item -> new ItemOrcamentoRequest(
                        item.getItemId(),
                        item.getQuantidade(),
                        item.getTipo().name(),
                        item.getNome(),
                        item.getDescricao(),
                        item.getPreco() // Assuming preco in ItemOrdemServico maps to precoVenda in ItemOrcamentoRequest
                ))
                .toList();

        var clienteRequest = new ClienteRequest(
                ordemServico.getCliente().getId(),
                ordemServico.getCliente().getNomeCompleto(),
                ordemServico.getCliente().getCpfCnpj()
        );

        var veiculoRequest = new VeiculoRequest(
                ordemServico.getVeiculo().getId(),
                ordemServico.getVeiculo().getMarca(),
                ordemServico.getVeiculo().getModelo(),
                ordemServico.getVeiculo().getPlaca()
        );

        var request = new CriarOrcamentoRequest(
                clienteRequest,
                veiculoRequest,
                itens,
                ordemServico.getValorTotal(),
                ordemServico.getId()
        );
        try {
            CriarOrcamentoResponse orcamentoResponse = orcamentoFeignClient.criar(request);
            UUID orcamentoId = orcamentoResponse.id();
            logger.info("Orçamento ID {} criado com sucesso para Ordem de Serviço ID {}.", orcamentoId, ordemServico.getId());
            return orcamentoId;
        } catch (Exception e) {
            logger.error("Erro ao criar orçamento para Ordem de Serviço ID {}: {}", ordemServico.getId(), e.getMessage(), e);
            throw new RuntimeException("Erro ao criar orçamento.", e);
        }
    }
}
