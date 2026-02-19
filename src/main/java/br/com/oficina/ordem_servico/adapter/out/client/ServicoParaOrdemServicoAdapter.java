package br.com.oficina.ordem_servico.adapter.out.client;

import br.com.oficina.ordem_servico.adapter.out.client.feign.CatalogoFeignClient;
import br.com.oficina.ordem_servico.core.domain.entity.Servico;
import br.com.oficina.ordem_servico.core.port.out.OrdemServicoServicoPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ServicoParaOrdemServicoAdapter implements OrdemServicoServicoPort {

    private static final Logger logger = LoggerFactory.getLogger(ServicoParaOrdemServicoAdapter.class);
    private final CatalogoFeignClient catalogoFeignClient;

    @Override
    public Optional<Servico> buscarServicoPorId(UUID id) {
        logger.info("Tentando buscar serviço com ID: {}", id);
        try {
            var response = catalogoFeignClient.buscarServicoPorId(id);
            logger.info("Serviço com ID {} encontrado com sucesso.", id);
            return Optional.of(new Servico(
                    response.id(),
                    response.nome(),
                    response.descricao(),
                    response.preco()
            ));
        } catch (Exception e) {
            logger.error("Erro ao buscar serviço com ID {}: {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
