package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.ItemOrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.OrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Status;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.TipoItem;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoNaoEncontradaException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoCatalogoPort;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.dto.ItemParaBaixaEstoque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecutarOrdemServicoUseCaseImplTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;
    @Mock
    private OrdemServicoCatalogoPort ordemServicoCatalogoPort;
    // OrdemServicoOrcamentoPort is not directly used in the modified execute method,
    // so it doesn't need to be mocked here if it's not being tested for its behavior.
    // private OrdemServicoOrcamentoPort ordemServicoOrcamentoPort;

    @InjectMocks
    private ExecutarOrdemServicoUseCaseImpl executarOrdemServicoUseCase;

    private UUID osId;
    private UUID orcamentoId;
    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {
        osId = UUID.randomUUID();
        orcamentoId = UUID.randomUUID();

        ItemOrdemServico pecaItem = ItemOrdemServico.builder()
                .itemId(UUID.randomUUID())
                .quantidade(2)
                .tipo(TipoItem.PECA)
                .preco(BigDecimal.TEN)
                .nome("Peca Teste")
                .build();

        ItemOrdemServico servicoItem = ItemOrdemServico.builder()
                .itemId(UUID.randomUUID())
                .quantidade(1)
                .tipo(TipoItem.SERVICO)
                .preco(BigDecimal.valueOf(50))
                .nome("Servico Teste")
                .build();

        ordemServico = OrdemServico.from(
                osId,
                null,
                null,
                List.of(pecaItem, servicoItem),
                Status.AGUARDANDO_EXECUCAO,
                null, null, null, null, null, null
        );
    }

    @Test
    void shouldExecuteOrderAndReduceStockWhenPecaItemsExist() {
        // Given
        when(ordemServicoRepository.buscarPorId(osId)).thenReturn(Optional.of(ordemServico));
        doNothing().when(ordemServicoCatalogoPort).darBaixaEstoque(anyList());

        // When
        executarOrdemServicoUseCase.executar(osId, orcamentoId);

        // Then
        verify(ordemServicoRepository, times(1)).buscarPorId(osId);
        ArgumentCaptor<List<ItemParaBaixaEstoque>> captor = ArgumentCaptor.forClass(List.class);
        verify(ordemServicoCatalogoPort, times(1)).darBaixaEstoque(captor.capture());

        List<ItemParaBaixaEstoque> capturedItems = captor.getValue();
        assertEquals(1, capturedItems.size());
        assertEquals(TipoItem.PECA.name(), ordemServico.getItens().get(0).getTipo().name());
        assertEquals(ordemServico.getItens().get(0).getItemId(), capturedItems.get(0).getItemId());
        assertEquals(ordemServico.getItens().get(0).getQuantidade(), capturedItems.get(0).getQuantidade());


        // Verify that the order's execute method was called (status changed)
        assertEquals(Status.EM_EXECUCAO, ordemServico.getStatus());
        verify(ordemServicoRepository, times(1)).salvar(ordemServico);
    }

    @Test
    void shouldExecuteOrderWithoutReducingStockWhenNoPecaItemsExist() {
        // Given
        ordemServico = OrdemServico.from(
                osId,
                null,
                null,
                Collections.singletonList(ItemOrdemServico.builder()
                        .itemId(UUID.randomUUID())
                        .quantidade(1)
                        .tipo(TipoItem.SERVICO)
                        .preco(BigDecimal.valueOf(50))
                        .nome("Servico Teste")
                        .build()),
                Status.AGUARDANDO_EXECUCAO,
                null, null, null, null, null, null
        );
        when(ordemServicoRepository.buscarPorId(osId)).thenReturn(Optional.of(ordemServico));

        // When
        executarOrdemServicoUseCase.executar(osId, orcamentoId);

        // Then
        verify(ordemServicoRepository, times(1)).buscarPorId(osId);
        verify(ordemServicoCatalogoPort, never()).darBaixaEstoque(anyList()); // No peca items, so no stock reduction
        assertEquals(Status.EM_EXECUCAO, ordemServico.getStatus());
        verify(ordemServicoRepository, times(1)).salvar(ordemServico);
    }

    @Test
    void shouldThrowExceptionWhenStockReductionFails() {
        // Given
        when(ordemServicoRepository.buscarPorId(osId)).thenReturn(Optional.of(ordemServico));
        doThrow(new RuntimeException("Estoque insuficiente"))
                .when(ordemServicoCatalogoPort).darBaixaEstoque(anyList());

        // When & Then
        OrdemServicoException exception = assertThrows(OrdemServicoException.class, () ->
                executarOrdemServicoUseCase.executar(osId, orcamentoId));

        assertEquals("Falha ao dar baixa no estoque de peças: Estoque insuficiente", exception.getMessage());
        verify(ordemServicoRepository, times(1)).buscarPorId(osId);
        verify(ordemServicoCatalogoPort, times(1)).darBaixaEstoque(anyList());
        verify(ordemServicoRepository, never()).salvar(any(OrdemServico.class)); // Should not save if stock reduction fails
    }

    @Test
    void shouldThrowExceptionWhenOrderServiceNotFound() {
        // Given
        when(ordemServicoRepository.buscarPorId(osId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(OrdemServicoNaoEncontradaException.class, () ->
                executarOrdemServicoUseCase.executar(osId, orcamentoId));

        verify(ordemServicoRepository, times(1)).buscarPorId(osId);
        verify(ordemServicoCatalogoPort, never()).darBaixaEstoque(anyList());
        verify(ordemServicoRepository, never()).salvar(any(OrdemServico.class));
    }

    @Test
    void shouldThrowExceptionWhenOrderIsAwaitingApproval() {
        // Given
        ordemServico = OrdemServico.from(
                osId,
                null,
                null,
                Collections.emptyList(),
                Status.AGUARDANDO_APROVACAO, // Set status to awaiting approval
                null, null, null, null, null, null
        );
        when(ordemServicoRepository.buscarPorId(osId)).thenReturn(Optional.of(ordemServico));

        // When & Then
        OrdemServicoException exception = assertThrows(OrdemServicoException.class, () ->
                executarOrdemServicoUseCase.executar(osId, orcamentoId));

        assertEquals("Ordem de servico com aprovação do orçamento pendente", exception.getMessage());
        verify(ordemServicoRepository, times(1)).buscarPorId(osId);
        verify(ordemServicoCatalogoPort, never()).darBaixaEstoque(anyList());
        verify(ordemServicoRepository, never()).salvar(any(OrdemServico.class));
    }
}
