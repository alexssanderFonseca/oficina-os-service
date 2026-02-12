package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.CatalogoFeignClient;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Servico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoServicoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ServicoParaOrdemServicoAdapter implements OrdemServicoServicoPort {

    private final CatalogoFeignClient catalogoFeignClient;

    @Override
    public Optional<Servico> buscarServicoPorId(UUID id) {
        try {
            var response = catalogoFeignClient.buscarServicoPorId(id);
            return Optional.of(new Servico(
                    response.id(),
                    response.nome(),
                    response.descricao(),
                    response.preco()
            ));
        } catch (Exception e) {
            // logger.error("Error fetching servico with id {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }
}
