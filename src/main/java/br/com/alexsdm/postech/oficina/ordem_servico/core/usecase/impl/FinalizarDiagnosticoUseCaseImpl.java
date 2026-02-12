package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.ItemOrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.OrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.TipoItem;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoNaoEncontradaException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.in.FinalizarDiagnosticoUseCase;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoCatalogoPort;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoOrcamentoPort;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.dto.ItemCatalogoDto;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.input.FinalizarDiagnosticoInput;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output.FinalizarDiagnosticoOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FinalizarDiagnosticoUseCaseImpl implements FinalizarDiagnosticoUseCase {

    private final OrdemServicoCatalogoPort catalogoPort;
    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoOrcamentoPort ordemServicoOrcamentoPort;


    @Override
    public FinalizarDiagnosticoOutput executar(FinalizarDiagnosticoInput input) {

        var ordemServico = ordemServicoRepository.buscarPorId(input.osId())
                .orElseThrow(OrdemServicoNaoEncontradaException::new);

        var itens = obterItens(input);
        ordemServico.finalizarDiagnostico(itens);
        ordemServicoRepository.salvar(ordemServico);
        enviarOrcamento(ordemServico);
        return new FinalizarDiagnosticoOutput(UUID.randomUUID());
    }

    private List<ItemOrdemServico> obterItens(FinalizarDiagnosticoInput input) {

        return input.itens()
                .stream()
                .map(itemOrdemServicoInput ->
                        CompletableFuture.supplyAsync(() -> {
                                    var itemCatalogo = catalogoPort.buscarItemCatalogo(itemOrdemServicoInput.id(),
                                            itemOrdemServicoInput.tipo());
                                    return montarItemOrdemServico(itemCatalogo, itemOrdemServicoInput);
                                }
                        ))
                .map(CompletableFuture::join)
                .toList();


    }

    private ItemOrdemServico montarItemOrdemServico(ItemCatalogoDto itemCatalogoDto,
                                                    FinalizarDiagnosticoInput.ItemOrdemServicoInput itemInput) {
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


    private void enviarOrcamento(OrdemServico ordemServico) {
        ordemServicoOrcamentoPort.criar(ordemServico);
    }


}

