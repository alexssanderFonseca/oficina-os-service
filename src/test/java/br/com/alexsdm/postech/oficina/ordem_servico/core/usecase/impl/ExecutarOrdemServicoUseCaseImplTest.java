package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.*;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoNaoEncontradaException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoCatalogoPort;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoPagamentoPort;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecutarOrdemServicoUseCaseImplTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;
    @Mock
    private OrdemServicoCatalogoPort ordemServicoCatalogoPort;
    @Mock
    private OrdemServicoPagamentoPort ordemServicoPagamentoPort; // Novo mock

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
                LocalDateTime.now().minusDays(1), null, null, null, null, null,
                null, // StatusPagamento inicial
                null // PagamentoId inicial
        );
        ordemServico.vincularOrcamento(orcamentoId);
    }

    @Test
    void shouldExecuteOrderAndInitiatePaymentWhenPecaItemsExist() {
        // Given
        when(ordemServicoRepository.buscarPorId(osId)).thenReturn(Optional.of(ordemServico));
        doNothing().when(ordemServicoCatalogoPort).darBaixaEstoque(anyList());
        doNothing().when(ordemServicoPagamentoPort).solicitarPagamento(any(OrdemServico.class));

        // When
        executarOrdemServicoUseCase.executar(osId, orcamentoId);

        // Then
        InOrder inOrder = inOrder(ordemServicoRepository, ordemServicoCatalogoPort, ordemServicoPagamentoPort);

        inOrder.verify(ordemServicoRepository, times(1)).buscarPorId(osId);
        inOrder.verify(ordemServicoCatalogoPort, times(1)).darBaixaEstoque(anyList());
        inOrder.verify(ordemServicoRepository, times(1)).salvar(ordemServico); // Salva após iniciar pagamento
        inOrder.verify(ordemServicoPagamentoPort, times(1)).solicitarPagamento(ordemServico);

        // Verify that the order's execute method was called (status changed)
        assertEquals(Status.EM_EXECUCAO, ordemServico.getStatus());
        assertEquals(PagamentoStatus.PENDENTE, ordemServico.getStatusPagamento());
    }

    @Test
    void shouldExecuteOrderWithoutReducingStockButInitiatePaymentWhenNoPecaItemsExist() {
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
                LocalDateTime.now().minusDays(1), null, null, null, null, null,
                null, // StatusPagamento inicial
                null // PagamentoId inicial
        );
        ordemServico.vincularOrcamento(orcamentoId);
        when(ordemServicoRepository.buscarPorId(osId)).thenReturn(Optional.of(ordemServico));
        doNothing().when(ordemServicoPagamentoPort).solicitarPagamento(any(OrdemServico.class));


        // When
        executarOrdemServicoUseCase.executar(osId, orcamentoId);

        // Then
        InOrder inOrder = inOrder(ordemServicoRepository, ordemServicoCatalogoPort, ordemServicoPagamentoPort);

        inOrder.verify(ordemServicoRepository, times(1)).buscarPorId(osId);
        inOrder.verify(ordemServicoCatalogoPort, never()).darBaixaEstoque(anyList()); // No peca items, so no stock reduction
        inOrder.verify(ordemServicoRepository, times(1)).salvar(ordemServico); // Salva após iniciar pagamento
        inOrder.verify(ordemServicoPagamentoPort, times(1)).solicitarPagamento(ordemServico);

        assertEquals(Status.EM_EXECUCAO, ordemServico.getStatus());
        assertEquals(PagamentoStatus.PENDENTE, ordemServico.getStatusPagamento());
    }

    @Test
    void shouldThrowExceptionWhenStockReductionFailsAndNotInitiatePayment() {
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
        verify(ordemServicoPagamentoPort, never()).solicitarPagamento(any(OrdemServico.class)); // Payment not initiated
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
        verify(ordemServicoPagamentoPort, never()).solicitarPagamento(any(OrdemServico.class));
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
                LocalDateTime.now().minusDays(1), null, null, null, null, null,
                null, // StatusPagamento inicial
                null // PagamentoId inicial
        );
        ordemServico.vincularOrcamento(orcamentoId);
        when(ordemServicoRepository.buscarPorId(osId)).thenReturn(Optional.of(ordemServico));

        // When & Then
        OrdemServicoException exception = assertThrows(OrdemServicoException.class, () ->
                executarOrdemServicoUseCase.executar(osId, orcamentoId));

        assertEquals("Ordem de servico com aprovação do orçamento pendente", exception.getMessage());
        verify(ordemServicoRepository, times(1)).buscarPorId(osId);
        verify(ordemServicoCatalogoPort, never()).darBaixaEstoque(anyList());
        verify(ordemServicoPagamentoPort, never()).solicitarPagamento(any(OrdemServico.class));
        verify(ordemServicoRepository, never()).salvar(any(OrdemServico.class));
    }

    @Test
    void shouldThrowExceptionWhenPaymentInitiationFails() {
        // Given
        when(ordemServicoRepository.buscarPorId(osId)).thenReturn(Optional.of(ordemServico));
        doNothing().when(ordemServicoCatalogoPort).darBaixaEstoque(anyList());
        doThrow(new RuntimeException("Erro ao iniciar pagamento"))
                .when(ordemServicoPagamentoPort).solicitarPagamento(any(OrdemServico.class));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                executarOrdemServicoUseCase.executar(osId, orcamentoId));

        assertEquals("Erro ao iniciar pagamento", exception.getMessage());
        verify(ordemServicoRepository, times(1)).buscarPorId(osId);
        verify(ordemServicoCatalogoPort, times(1)).darBaixaEstoque(anyList());
        verify(ordemServicoPagamentoPort, times(1)).solicitarPagamento(any(OrdemServico.class));
        // Expect one save for initial status update, but no second save if payment fails later.
        // The current implementation saves before calling payment port, so one save is expected.
        verify(ordemServicoRepository, times(1)).salvar(ordemServico);
    }
}

