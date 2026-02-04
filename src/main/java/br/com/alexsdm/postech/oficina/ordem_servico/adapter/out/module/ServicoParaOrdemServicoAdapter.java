package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.module;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Servico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoServicoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ServicoParaOrdemServicoAdapter implements OrdemServicoServicoPort {


    @Override
    public Optional<Servico> buscarServicoPorId(UUID id) {
//        try {
//            var servico = buscarServicoPorIdUseCase.executar(id);
//            return Optional.of(new Servico(servico.getId(),
//                    servico.getNome(),
//                    servico.getDescricao(),
//                    servico.getPreco()));
//        } catch (Exception e) {
//            return Optional.empty();
//        }

        return null;
    }
}
