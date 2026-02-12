package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.*;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoClienteNaoEncontradoException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoVeiculoNaoEncontradoException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.in.AbrirOrdemServicoUseCase;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoCatalogoPort;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoClientePort;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoMetricPort;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.dto.ItemCatalogoDto;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.input.CriarOrdemServicoInput;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Named
@RequiredArgsConstructor
public class AbrirOrdemServicoUseCaseImpl implements AbrirOrdemServicoUseCase {

    private final OrdemServicoCatalogoPort itemCatologoPort;
    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoClientePort ordemServicoClientePort;
    private final OrdemServicoMetricPort ordemServicoMetricPort;

    @Override
    public UUID executar(CriarOrdemServicoInput input) {
        var cliente = ordemServicoClientePort.buscarCliente(input.clienteId())
                .orElseThrow(OrdemServicoClienteNaoEncontradoException::new);

        var veiculo = cliente.getVeiculos().stream()
                .filter(v -> v.getId().equals(input.veiculoId()))
                .findFirst()
                .orElseThrow(OrdemServicoVeiculoNaoEncontradoException::new);

        var ordemServico = abrirOrdemServico(cliente, veiculo, input);
        ordemServicoRepository.salvar(ordemServico);
        ordemServicoMetricPort.incrementaNumeroOrdensCriadas();
        return ordemServico.getId();
    }

    private OrdemServico abrirOrdemServico(Cliente cliente, Veiculo veiculo, CriarOrdemServicoInput input) {
        var possuiItens = !input.itens().isEmpty();
        if (possuiItens) {
            return abrirOrdemServicoParaExecucao(cliente, veiculo, input);
        }

        return abrirOrdemServicoParaDiagnostico(cliente, veiculo);
    }


    private OrdemServico abrirOrdemServicoParaDiagnostico(Cliente cliente,
                                                          Veiculo veiculo) {
        return OrdemServico.criarOrdemServicoParaDiagnostico(cliente, veiculo);
    }

    private OrdemServico abrirOrdemServicoParaExecucao(Cliente cliente,
                                                       Veiculo veiculo,
                                                       CriarOrdemServicoInput input) {


        var itens = input.itens()
                .stream()
                .map(itemOrdemServicoInput ->
                        CompletableFuture.supplyAsync(() -> {
                                    var itemCatalogo = itemCatologoPort.buscarItemCatalogo(itemOrdemServicoInput.id(),
                                            itemOrdemServicoInput.tipo());
                                    return montarItemOrdemServico(itemCatalogo, itemOrdemServicoInput);
                                }
                        ))
                .map(CompletableFuture::join)
                .toList();


        return OrdemServico.criarOrdemServicoParaExecucao(
                cliente,
                veiculo,
                itens
        );
    }

    private ItemOrdemServico montarItemOrdemServico(ItemCatalogoDto itemCatalogoDto,
                                                    CriarOrdemServicoInput.ItemInput itemInput) {
        return new ItemOrdemServico(
                UUID.randomUUID(),
                itemCatalogoDto.nome(),
                itemCatalogoDto.descricao(),
                itemCatalogoDto.precoVenda(),
                itemInput.quantidade(),
                itemCatalogoDto.id(),
                TipoItem.fromString(itemCatalogoDto.tipo())
        );
    }


}
