package br.com.oficina.ordem_servico.core.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrdemServicoTest {

    private OrdemServico ordemServico;
    private Cliente cliente;
    private Veiculo veiculo;
    private ItemOrdemServico itemPeca;
    private ItemOrdemServico itemServico;

    @BeforeEach
    void setUp() {
        cliente = new Cliente(UUID.randomUUID(), "João Silva", "123.456.789-00", Collections.emptyList());
        veiculo = new Veiculo(UUID.randomUUID(), "ABC-1234", "Fiat", "Palio", "2020", "Branco");
        itemPeca = ItemOrdemServico.builder()
                .id(UUID.randomUUID())
                .itemId(UUID.randomUUID())
                .tipo(TipoItem.PECA)
                .quantidade(2)
                .preco(new BigDecimal("50.00"))
                .nome("Pneu")
                .build();
        itemServico = ItemOrdemServico.builder()
                .id(UUID.randomUUID())
                .itemId(UUID.randomUUID())
                .tipo(TipoItem.SERVICO)
                .quantidade(1)
                .preco(new BigDecimal("100.00"))
                .nome("Troca de óleo")
                .build();

        List<ItemOrdemServico> itens = new ArrayList<>();
        itens.add(itemPeca);
        itens.add(itemServico);

        ordemServico = OrdemServico.from(
                UUID.randomUUID(),
                cliente,
                veiculo,
                itens,
                Status.AGUARDANDO_EXECUCAO,
                LocalDateTime.now().minusDays(1),
                null, null, null, null, null,
                PagamentoStatus.PENDENTE,
                null
        );
    }

    @Test
    void iniciarPagamentoDeveDefinirStatusComoPendente() {
        ordemServico.iniciarPagamento();
        assertEquals(PagamentoStatus.PENDENTE, ordemServico.getStatusPagamento());
    }

    @Test
    void confirmarPagamentoDeveDefinirStatusComoAprovadoESalvarId() {
        UUID pagamentoId = UUID.randomUUID();
        ordemServico.confirmarPagamento(pagamentoId);
        assertEquals(PagamentoStatus.APROVADO, ordemServico.getStatusPagamento());
        assertEquals(pagamentoId, ordemServico.getPagamentoId());
    }

    @Test
    void rejeitarPagamentoDeveDefinirStatusComoRecusado() {
        ordemServico.rejeitarPagamento();
        assertEquals(PagamentoStatus.RECUSADO, ordemServico.getStatusPagamento());
        // Considerando que rejeitar pagamento pode levar a um status específico da OS
        // assertEquals(Status.FALHA, ordemServico.getStatus()); // Se essa for a regra de negócio
    }

    @Test
    void finalizarDeveLancarExcecaoSePagamentoNaoAprovado() {
        ordemServico.rejeitarPagamento(); // Pagamento recusado
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ordemServico.finalizar());
        assertEquals("A ordem de serviço só pode ser finalizada se o pagamento estiver aprovado.", exception.getMessage());
    }

    @Test
    void finalizarDeveAlterarStatusParaFinalizadaSePagamentoAprovado() {
        ordemServico.confirmarPagamento(UUID.randomUUID()); // Pagamento aprovado
        ordemServico.finalizar();
        assertEquals(Status.FINALIZADA, ordemServico.getStatus());
        assertNotNull(ordemServico.getDataFinalizacao());
    }
}
