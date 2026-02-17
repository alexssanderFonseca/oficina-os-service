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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Named
@RequiredArgsConstructor
public class AbrirOrdemServicoUseCaseImpl implements AbrirOrdemServicoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(AbrirOrdemServicoUseCaseImpl.class);

    private final OrdemServicoCatalogoPort itemCatologoPort;
    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoClientePort ordemServicoClientePort;
    private final OrdemServicoMetricPort ordemServicoMetricPort;

    @Override
    public UUID executar(CriarOrdemServicoInput input) {
        logger.info("Iniciando a abertura de ordem de serviço para clienteId: {} e veiculoId: {}", input.clienteId(), input.veiculoId());

        logger.info("Buscando cliente com ID: {}", input.clienteId());
        var cliente = ordemServicoClientePort.buscarCliente(input.clienteId())
                .orElseGet(() -> {
                    logger.warn("Cliente com ID {} não encontrado.", input.clienteId());
                    throw new OrdemServicoClienteNaoEncontradoException();
                });
        logger.info("Cliente com ID {} encontrado.", cliente.getId());

        var veiculo = cliente.getVeiculos().stream()
                .filter(v -> v.getId().equals(input.veiculoId()))
                .findFirst()
                .orElseThrow(OrdemServicoVeiculoNaoEncontradoException::new);

        var ordemServico = abrirOrdemServico(cliente, veiculo, input);
        ordemServicoRepository.salvar(ordemServico);
        ordemServicoMetricPort.incrementaNumeroOrdensCriadas();
        logger.info("Ordem de serviço ID {} criada com sucesso para o cliente ID {} e veículo ID {}.", ordemServico.getId(), cliente.getId(), veiculo.getId());
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
