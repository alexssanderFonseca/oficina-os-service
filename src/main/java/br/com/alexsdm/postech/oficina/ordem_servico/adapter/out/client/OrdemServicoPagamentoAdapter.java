package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.PagamentoFeignClient;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.PagamentoRequest;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.OrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoPagamentoPort;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@RequiredArgsConstructor
public class OrdemServicoPagamentoAdapter implements OrdemServicoPagamentoPort {

    private static final Logger logger = LoggerFactory.getLogger(OrdemServicoPagamentoAdapter.class);
    private final PagamentoFeignClient pagamentoFeignClient;

    @Override
    public void solicitarPagamento(OrdemServico ordemServico) {
        try {
            // A descrição deve ser: concat do nome do cliente + veiculo, + data + valor
            String description = String.format("OS %s - Cliente: %s - Veiculo: %s - Valor: %.2f",
                    ordemServico.getId().toString(),
                    ordemServico.getCliente().getNomeCompleto(),
                    ordemServico.getVeiculo().getModelo(),
                    ordemServico.getValorTotal().doubleValue());

            PagamentoRequest request = PagamentoRequest.builder()
                    .external_reference(ordemServico.getId().toString())
                    .amount(ordemServico.getValorTotal().doubleValue())
                    .description(description)
                    .build();

            logger.info("Solicitando pagamento para OS ID: {}", ordemServico.getId());
            pagamentoFeignClient.solicitarPagamento(request);
            logger.info("Pagamento solicitado com sucesso para OS ID: {}", ordemServico.getId());
        } catch (Exception e) {
            logger.error("Falha ao solicitar pagamento para OS ID {}: {}", ordemServico.getId(), e.getMessage(), e);
            throw new RuntimeException("Falha ao solicitar pagamento: " + e.getMessage(), e);
        }
    }
}
