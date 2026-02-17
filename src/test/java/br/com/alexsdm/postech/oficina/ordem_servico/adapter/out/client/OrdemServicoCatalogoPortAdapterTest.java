package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.CatalogoFeignClient;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.BaixaEstoqueRequest;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.dto.ItemParaBaixaEstoque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdemServicoCatalogoPortAdapterTest {

    @Mock
    private CatalogoFeignClient catalogoFeignClient;

    @InjectMocks
    private OrdemServicoCatalogoPortAdapter adapter;

    private UUID itemId;
    private ItemParaBaixaEstoque itemParaBaixaEstoque;
    private List<ItemParaBaixaEstoque> itemsParaBaixa;

    @BeforeEach
    void setUp() {
        itemId = UUID.randomUUID();
        itemParaBaixaEstoque = ItemParaBaixaEstoque.builder()
                .itemId(itemId)
                .quantidade(5)
                .build();
        itemsParaBaixa = Collections.singletonList(itemParaBaixaEstoque);
    }

    @Test
    void shouldCallFeignClientToReduceStockSuccessfully() {
        // Given
        doNothing().when(catalogoFeignClient).darBaixaEstoque(anyList());

        // When
        adapter.darBaixaEstoque(itemsParaBaixa);

        // Then
        ArgumentCaptor<List<BaixaEstoqueRequest>> captor = ArgumentCaptor.forClass(List.class);
        verify(catalogoFeignClient, times(1)).darBaixaEstoque(captor.capture());

        List<BaixaEstoqueRequest> capturedRequests = captor.getValue();
        assertEquals(1, capturedRequests.size());
        assertEquals(itemId, capturedRequests.get(0).getItemId());
        assertEquals(5, capturedRequests.get(0).getQuantidade());
    }

    @Test
    void shouldPropagateExceptionWhenFeignClientFails() {
        // Given
        RuntimeException feignException = new RuntimeException("Feign client error");
        doThrow(feignException).when(catalogoFeignClient).darBaixaEstoque(anyList());

        // When & Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                adapter.darBaixaEstoque(itemsParaBaixa));

        assertEquals("Erro ao solicitar baixa de estoque.", thrown.getMessage());
        verify(catalogoFeignClient, times(1)).darBaixaEstoque(anyList());
    }
}
