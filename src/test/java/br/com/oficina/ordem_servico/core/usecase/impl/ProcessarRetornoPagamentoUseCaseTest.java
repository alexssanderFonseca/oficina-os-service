package br.com.oficina.ordem_servico.core.usecase.impl;

import br.com.oficina.ordem_servico.core.domain.entity.*;
import br.com.oficina.ordem_servico.core.domain.entity.*;
import br.com.oficina.ordem_servico.core.domain.exception.OrdemServicoException;
import br.com.oficina.ordem_servico.core.domain.exception.OrdemServicoNaoEncontradaException;
import br.com.oficina.ordem_servico.core.port.out.OrdemServicoCatalogoPort;
import br.com.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import br.com.oficina.ordem_servico.core.usecase.dto.events.PagamentoEventoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarRetornoPagamentoUseCaseTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;
    @Mock
    private OrdemServicoCatalogoPort ordemServicoCatalogoPort;

    @InjectMocks
    private ProcessarRetornoPagamentoUseCase processarRetornoPagamentoUseCase;

    private UUID osId;
    private UUID pagamentoId;
    private OrdemServico ordemServico;
    private ItemOrdemServico pecaItem;

    @BeforeEach
    void setUp() {
        osId = UUID.randomUUID();
        pagamentoId = UUID.randomUUID();

        pecaItem = ItemOrdemServico.builder()
                .itemId(UUID.randomUUID())
                .quantidade(2)
                .tipo(TipoItem.PECA)
                .preco(BigDecimal.TEN)
                .nome("Peca Teste")
                .build();

        ordemServico = OrdemServico.from(
                osId,
                null,
                null,
                List.of(pecaItem),
                Status.AGUARDANDO_EXECUCAO,
                LocalDateTime.now(), null, null, null, null, null,
                PagamentoStatus.PENDENTE,
                null
        );
    }

    @Test
    void shouldConfirmPaymentWhenStatusIsAprovado() {
        // Given
        PagamentoEventoDTO evento = new PagamentoEventoDTO(osId, "APROVADO", pagamentoId);
        when(ordemServicoRepository.buscarPorId(osId)).thenReturn(Optional.of(ordemServico));

        // When
        processarRetornoPagamentoUseCase.execute(evento);

        // Then
        verify(ordemServicoRepository, times(1)).buscarPorId(osId);
        verify(ordemServicoRepository, times(1)).salvar(ordemServico);
        verify(ordemServicoCatalogoPort, never()).reporEstoque(anyList());
        assertEquals(PagamentoStatus.APROVADO, ordemServico.getStatusPagamento());
        assertEquals(pagamentoId, ordemServico.getPagamentoId());
    }

    @Test
    void shouldRejectPaymentAndReporEstoqueWhenStatusIsRecusado() {
        // Given
        PagamentoEventoDTO evento = new PagamentoEventoDTO(osId, "RECUSADO", pagamentoId);
        when(ordemServicoRepository.buscarPorId(osId)).thenReturn(Optional.of(ordemServico));
        doNothing().when(ordemServicoCatalogoPort).reporEstoque(anyList());

        // When
        processarRetornoPagamentoUseCase.execute(evento);

        // Then
        verify(ordemServicoRepository, times(1)).buscarPorId(osId);
        verify(ordemServicoRepository, times(1)).salvar(ordemServico);
        verify(ordemServicoCatalogoPort, times(1)).reporEstoque(anyList());
        assertEquals(PagamentoStatus.RECUSADO, ordemServico.getStatusPagamento());
        assertEquals(Status.AGUARDANDO_PAGAMENTO, ordemServico.getStatus()); // Confirma que o status da OS mudou para FALHA
    }

    @Test
    void shouldThrowExceptionWhenStatusIsUnknown() {
        // Given
        PagamentoEventoDTO evento = new PagamentoEventoDTO(osId, "UNKNOWN_STATUS", pagamentoId);
        when(ordemServicoRepository.buscarPorId(osId)).thenReturn(Optional.of(ordemServico));

        // When & Then
        OrdemServicoException exception = assertThrows(OrdemServicoException.class, () ->
                processarRetornoPagamentoUseCase.execute(evento));

        assertEquals("Status de pagamento desconhecido.", exception.getMessage());
        verify(ordemServicoRepository, times(1)).buscarPorId(osId);
        verify(ordemServicoRepository, never()).salvar(any(OrdemServico.class));
        verify(ordemServicoCatalogoPort, never()).reporEstoque(anyList());
    }

    @Test
    void shouldThrowExceptionWhenOrdemServicoNotFound() {
        // Given
        PagamentoEventoDTO evento = new PagamentoEventoDTO(osId, "APROVADO", pagamentoId);
        when(ordemServicoRepository.buscarPorId(osId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(OrdemServicoNaoEncontradaException.class, () ->
                processarRetornoPagamentoUseCase.execute(evento));

        verify(ordemServicoRepository, times(1)).buscarPorId(osId);
        verify(ordemServicoRepository, never()).salvar(any(OrdemServico.class));
        verify(ordemServicoCatalogoPort, never()).reporEstoque(anyList());
    }

    @Test
    void shouldReporEstoqueWhenNoPecaItemsFoundOnReject() {
        // Given
        PagamentoEventoDTO evento = new PagamentoEventoDTO(osId, "RECUSADO", pagamentoId);
        OrdemServico osSemPecas = OrdemServico.from(
                osId, null, null, List.of(
                        ItemOrdemServico.builder().itemId(UUID.randomUUID()).quantidade(1).tipo(TipoItem.SERVICO).preco(BigDecimal.valueOf(50)).nome("Servico Teste").build()
                ), Status.AGUARDANDO_EXECUCAO, LocalDateTime.now(), null, null, null, null, null,
                PagamentoStatus.PENDENTE, null
        );
        when(ordemServicoRepository.buscarPorId(osId)).thenReturn(Optional.of(osSemPecas));

        // When
        processarRetornoPagamentoUseCase.execute(evento);

        // Then
        verify(ordemServicoRepository, times(1)).buscarPorId(osId);
        verify(ordemServicoRepository, times(1)).salvar(osSemPecas);
        verify(ordemServicoCatalogoPort, never()).reporEstoque(anyList()); // Não deve chamar se não houver peças
        assertEquals(PagamentoStatus.RECUSADO, osSemPecas.getStatusPagamento());
    }
}
