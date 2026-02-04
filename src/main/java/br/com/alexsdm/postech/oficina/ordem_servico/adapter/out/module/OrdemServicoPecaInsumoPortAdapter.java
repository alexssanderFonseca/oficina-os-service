package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.module;

import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoPecaInsumoPort;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.PecaInsumo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrdemServicoPecaInsumoPortAdapter implements OrdemServicoPecaInsumoPort {

    //private final BuscarPecaInsumoPorIdUseCase buscarPecaInsumoPorIdUseCase;

    @Override
    public Optional<PecaInsumo> buscarPecaInsumo(UUID id) {
//        try {
//            var buscarPecaInsumoOutput = buscarPecaInsumoPorIdUseCase.executar(id);
//
//            return Optional.of(new PecaInsumo(
//                    buscarPecaInsumoOutput.id(),
//                    buscarPecaInsumoOutput.nome(),
//                    buscarPecaInsumoOutput.descricao(),
//                    buscarPecaInsumoOutput.quantidadeEstoque(),
//                    buscarPecaInsumoOutput.precoVenda()
//            ));
//        } catch (Exception e) {
//            return Optional.empty();
//        }

        return null;

    }
}
