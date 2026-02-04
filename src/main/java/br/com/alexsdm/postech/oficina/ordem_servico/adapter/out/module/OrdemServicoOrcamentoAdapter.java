package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.module;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Orcamento;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoOrcamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrdemServicoOrcamentoAdapter implements OrdemServicoOrcamentoPort {

//    private final BuscarOrcamentoPorIdUseCase buscarOrcamentoPorIdUseCase;
//    private final CriarOrcamentoUseCase criarOrcamentoUseCase;

    @Override
    public Optional<Orcamento> buscarPorId(UUID id) {
//        var buscarOrcamentoOutput = buscarOrcamentoPorIdUseCase.executar(id);
//
//
//        // TODO REVISTAR
//        var servicos = buscarOrcamentoOutput.servicos()
//                .stream()
//                .map(orcamentoServico -> new ItemServicoOrcamento(
//                        null,
//                        orcamentoServico.id(),
//                        orcamentoServico.nome(),
//                        orcamentoServico.descricao(),
//                        orcamentoServico.valor()
//                )).toList();
//
//        var pecas = buscarOrcamentoOutput.pecas()
//                .stream()
//                .map(itemPeca -> new ItemPecaOrcamento(
//                        itemPeca.id(),
//                        itemPeca.qtd(),
//                        itemPeca.nome(),
//                        itemPeca.descricao(),
//                        itemPeca.valorUnitario()
//                )).toList();
//
//        return Optional.of(new Orcamento(
//                buscarOrcamentoOutput.id(),
//                UUID.fromString(buscarOrcamentoOutput.cliente().id()),
//                UUID.fromString(buscarOrcamentoOutput.veiculo().id()),
//                buscarOrcamentoOutput.status(),
//                servicos,
//                pecas
//        ));

        return null;
    }

    @Override
    public UUID criar(Orcamento orcamento) {
//        var pecas = orcamento.getPecas()
//                .stream()
//                .map(itemPecaOrdemServico -> new CriarOrcamentoInput.CriarOrcamentoItemPecaInput(
//                        itemPecaOrdemServico.getPecaId(),
//                        itemPecaOrdemServico.getQuantidade()
//                )).toList();
//
//        var servicos = orcamento.getServicos()
//                .stream()
//                .map(ItemServicoOrcamento::getServicoId).toList();
//
//
//        var input = new CriarOrcamentoInput(
//                orcamento.getClienteId().toString(),
//                null,
//                orcamento.getVeiculoId(),
//                pecas,
//                servicos
//        );
//        return criarOrcamentoUseCase.executar(input);
//    }
        return null;
    }

}
