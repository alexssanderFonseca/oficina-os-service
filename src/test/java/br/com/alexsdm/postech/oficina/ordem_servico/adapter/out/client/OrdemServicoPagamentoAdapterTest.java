package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.PagamentoFeignClient;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.PagamentoRequest;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Cliente;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.ItemOrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.OrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Status;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.TipoItem;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Veiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdemServicoPagamentoAdapterTest {

    @Mock
    private PagamentoFeignClient pagamentoFeignClient;

    @InjectMocks
    private OrdemServicoPagamentoAdapter ordemServicoPagamentoAdapter;

    private OrdemServico ordemServico;
    private UUID osId;
    private Cliente cliente;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        osId = UUID.randomUUID();
        veiculo = new Veiculo(UUID.randomUUID(), "ABC-123", "Modelo Teste", "Marca Teste", "2020","branco");
        cliente = new Cliente(UUID.randomUUID(), "Nome Cliente Teste", "email@test.com", List.of(veiculo));

        ItemOrdemServico item1 = ItemOrdemServico.builder()
                .itemId(UUID.randomUUID())
                .quantidade(1)
                .tipo(TipoItem.PECA)
                .preco(new BigDecimal("100.00"))
                .nome("Peca Teste 1")
                .build();

        ItemOrdemServico item2 = ItemOrdemServico.builder()
                .itemId(UUID.randomUUID())
                .quantidade(2)
                .tipo(TipoItem.SERVICO)
                .preco(new BigDecimal("50.00"))
                .nome("Servico Teste 1")
                .build();

        ordemServico = OrdemServico.from(
                osId,
                cliente,
                veiculo,
                List.of(item1, item2),
                Status.EM_EXECUCAO,
                LocalDateTime.now(), null, null, null, null, null,
                null, null
        );
    }

    @Test
    void solicitarPagamentoDeveChamarFeignClientComRequestCorreto() {
        // Given
        doNothing().when(pagamentoFeignClient).solicitarPagamento(any(PagamentoRequest.class));

        // When
        ordemServicoPagamentoAdapter.solicitarPagamento(ordemServico);

        // Then
        ArgumentCaptor<PagamentoRequest> captor = ArgumentCaptor.forClass(PagamentoRequest.class);
        verify(pagamentoFeignClient, times(1)).solicitarPagamento(captor.capture());

        PagamentoRequest capturedRequest = captor.getValue();
        assertEquals(ordemServico.getId().toString(), capturedRequest.getExternal_reference());
        assertEquals(ordemServico.getValorTotal().doubleValue(), capturedRequest.getAmount());
        
        String expectedDescription = String.format("OS %s - Cliente: %s - Veiculo: %s - Valor: %.2f",
                ordemServico.getId().toString(),
                cliente.getNomeCompleto(),
                veiculo.getModelo(),
                ordemServico.getValorTotal().doubleValue());
        assertEquals(expectedDescription, capturedRequest.getDescription());
    }

    @Test
    void solicitarPagamentoDeveLancarExcecaoQuandoFeignClientFalhar() {
        // Given
        doThrow(new RuntimeException("Erro de comunicação com o serviço de pagamento"))
                .when(pagamentoFeignClient).solicitarPagamento(any(PagamentoRequest.class));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                ordemServicoPagamentoAdapter.solicitarPagamento(ordemServico));

        assertEquals("Falha ao solicitar pagamento: Erro de comunicação com o serviço de pagamento", exception.getMessage());
        verify(pagamentoFeignClient, times(1)).solicitarPagamento(any(PagamentoRequest.class));
    }
}
