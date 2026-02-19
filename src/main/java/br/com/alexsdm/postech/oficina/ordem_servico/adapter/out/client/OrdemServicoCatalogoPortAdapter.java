package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.CatalogoFeignClient;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.BaixaEstoqueRequest;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoCatalogoPort;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.dto.ItemCatalogoDto;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.dto.ItemParaBaixaEstoque;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrdemServicoCatalogoPortAdapter implements OrdemServicoCatalogoPort {

    private static final Logger logger = LoggerFactory.getLogger(OrdemServicoCatalogoPortAdapter.class);

    private final CatalogoFeignClient catalogoFeignClient;
    private static final String TIPO_PECA = "PECA";
    private static final String TIPO_SERVICO = "SERVICO";

    @Override
    public ItemCatalogoDto buscarItemCatalogo(UUID id, String tipoItem) {
        logger.info("Tentando buscar item de catálogo com ID: {} e tipo: {}", id, tipoItem);
        try {
            if (TIPO_PECA.equalsIgnoreCase(tipoItem)) {
                var response = catalogoFeignClient.buscarPecaPorId(id);
                if (response == null) throw new RuntimeException("Peça não encontrada no catálogo");
                logger.info("Peça com ID {} encontrada no catálogo.", id);
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
                if (response == null) throw new RuntimeException("Serviço não encontrado no catálogo");
                logger.info("Serviço com ID {} encontrado no catálogo.", id);
                return new ItemCatalogoDto(
                        response.id(),
                        response.nome(),
                        response.descricao(),
                        null,
                        response.preco(),
                        TIPO_SERVICO
                );
            }
            throw new RuntimeException("Tipo de item invalido");

        } catch (Exception e) {
            logger.error("Erro ao buscar item de catálogo com ID {} e tipo {}: {}", id, tipoItem, e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar item de catálogo.", e);
        }
    }

    @Override
    public void darBaixaEstoque(List<ItemParaBaixaEstoque> itens) {
        logger.info("Solicitando baixa de estoque para {} itens.", itens.size());
        try {
            var requests = itens.stream()
                    .map(item -> BaixaEstoqueRequest.builder()
                            .itemId(item.getItemId())
                            .quantidade(item.getQuantidade())
                            .build())
                    .toList();
            catalogoFeignClient.darBaixaEstoque(requests);
            logger.info("Baixa de estoque solicitada com sucesso para {} itens.", itens.size());
        } catch (Exception e) {
            logger.error("Erro ao solicitar baixa de estoque: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao solicitar baixa de estoque.", e);
        }
    }

    @Override
    public void reporEstoque(List<ItemParaBaixaEstoque> itens) {
        logger.info("Solicitando reposição de estoque para {} itens.", itens.size());
        try {
            var requests = itens.stream()
                    .map(item -> BaixaEstoqueRequest.builder()
                            .itemId(item.getItemId())
                            .quantidade(item.getQuantidade())
                            .build())
                    .toList();
            catalogoFeignClient.reporEstoque(requests);
            logger.info("Reposição de estoque solicitada com sucesso para {} itens.", itens.size());
        } catch (Exception e) {
            logger.error("Erro ao solicitar reposição de estoque: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao solicitar reposição de estoque.", e);
        }
    }
}
