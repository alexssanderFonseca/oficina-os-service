package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.CatalogoFeignClient;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoCatalogoPort;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.dto.ItemCatalogoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrdemServicoCatalogoPortAdapter implements OrdemServicoCatalogoPort {

    private final CatalogoFeignClient catalogoFeignClient;
    private static final String TIPO_PECA = "PECA";
    private static final String TIPO_SERVICO = "SERVICO";

    @Override
    public ItemCatalogoDto buscarItemCatalogo(UUID id, String tipoItem) {
        try {
            if (TIPO_PECA.equalsIgnoreCase(tipoItem)) {
                var response = catalogoFeignClient.buscarPecaPorId(id);

                return new ItemCatalogoDto(
                        response.id(),
                        response.nome(),
                        response.descricao(),
                        response.quantidadeEstoque(),
                        response.precoVenda(),
                        TIPO_PECA
                );
            }

            if (TIPO_SERVICO.equalsIgnoreCase(tipoItem)) {
                var response = catalogoFeignClient.buscarServicoPorId(id);

                return new ItemCatalogoDto(
                        response.id(),
                        response.nome(),
                        response.descricao(),
                        null,
                        response.preco(),
                        TIPO_SERVICO
                );
            }

            return null;

        } catch (Exception e) {
            // logger.error("Error fetching peca/insumo with id {}: {}", id, e.getMessage());
            throw new RuntimeException();
        }
    }
}
