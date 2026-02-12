package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.OrcamentoFeignClient;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.ClienteRequest;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.CriarOrcamentoRequest;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.ItemOrcamentoRequest;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.VeiculoRequest;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.OrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoOrcamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrdemServicoOrcamentoAdapter implements OrdemServicoOrcamentoPort {

    private final OrcamentoFeignClient orcamentoFeignClient;

    @Override
    public UUID criar(OrdemServico ordemServico) {
        var itens = ordemServico.getItens().stream()
                .map(item -> new ItemOrcamentoRequest(item.getItemId(), item.getQuantidade(), item.getTipo()))
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
                ordemServico.getValorTotal()
        );
        return orcamentoFeignClient.criar(request);
    }
}
