package br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class OrdemServico {
    private UUID id;
    private Cliente cliente;
    private Veiculo veiculo;
    private final List<ItemOrdemServico> itens = new ArrayList<>();
    private Status status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataInicioDiagnostico;
    private LocalDateTime dataFimDiagnostico;
    private LocalDateTime dataInicioDaExecucao;
    private LocalDateTime dataEntrega;
    private LocalDateTime dataFinalizacao;
    private BigDecimal valorTotal;
    private UUID orcamentoId;
    private PagamentoStatus statusPagamento;
    private UUID pagamentoId;

    private OrdemServico(UUID id,
                         Cliente cliente,
                         Veiculo veiculo,
                         List<ItemOrdemServico> itens,
                         Status status,
                         LocalDateTime dataCriacao,
                         LocalDateTime dataInicioDiagnostico,
                         LocalDateTime dataFimDiagnostico,
                         LocalDateTime dataInicioDaExecucao,
                         LocalDateTime dataEntrega,
                         LocalDateTime dataFinalizacao,
                         PagamentoStatus statusPagamento,
                         UUID pagamentoId) {
        this.id = id;
        this.cliente = cliente;
        this.veiculo = veiculo;
        if (itens != null) {
            this.itens.addAll(itens);
        }
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.dataInicioDiagnostico = dataInicioDiagnostico;
        this.dataFimDiagnostico = dataFimDiagnostico;
        this.dataInicioDaExecucao = dataInicioDaExecucao;
        this.dataEntrega = dataEntrega;
        this.dataFinalizacao = dataFinalizacao;
        this.valorTotal = calcularValorTotal();
        this.statusPagamento = statusPagamento;
        this.pagamentoId = pagamentoId;
    }


    public static OrdemServico criarOrdemServicoParaDiagnostico(Cliente cliente,
                                                                Veiculo veiculo) {
        return new OrdemServico(
                UUID.randomUUID(),
                cliente,
                veiculo,
                Collections.emptyList(),
                Status.EM_DIAGNOSTICO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static OrdemServico criarOrdemServicoParaExecucao(Cliente cliente,
                                                             Veiculo veiculo,
                                                             List<ItemOrdemServico> itens
    ) {
        return new OrdemServico(
                UUID.randomUUID(),
                cliente,
                veiculo,
                itens,
                Status.EM_EXECUCAO,
                LocalDateTime.now(),
                null,
                null,
                LocalDateTime.now(),
                null,
                null,
                null,
                null
        );
    }

    public static OrdemServico from(UUID id,
                                    Cliente cliente,
                                    Veiculo veiculo,
                                    List<ItemOrdemServico> itens,
                                    Status status,
                                    LocalDateTime dataCriacao,
                                    LocalDateTime dataInicioDiagnostico,
                                    LocalDateTime dataFimDiagnostico,
                                    LocalDateTime dataInicioDaExecucao,
                                    LocalDateTime dataEntrega,
                                    LocalDateTime dataFinalizacao,
                                    PagamentoStatus statusPagamento,
                                    UUID pagamentoId) {
        return new OrdemServico(
                id,
                cliente,
                veiculo,
                itens,
                status,
                dataCriacao,
                dataInicioDiagnostico,
                dataFimDiagnostico,
                dataInicioDaExecucao,
                dataEntrega,
                dataFinalizacao,
                statusPagamento,
                pagamentoId);

    }


    private void adicionarItens(List<ItemOrdemServico> itens) {
        this.itens.addAll(itens);
        this.calcularValorTotal();
    }


    public void executar() {
        this.status = Status.EM_EXECUCAO;
        this.dataInicioDaExecucao = LocalDateTime.now();
    }

    public void executar(List<ItemOrdemServico> itens) {
        this.adicionarItens(itens);
        this.executar();
    }

    public void iniciarPagamento() {
        this.statusPagamento = PagamentoStatus.PENDENTE;
    }

    public void confirmarPagamento(UUID pagamentoId) {
        this.statusPagamento = PagamentoStatus.APROVADO;
        this.pagamentoId = pagamentoId;
    }

    public void rejeitarPagamento() {
        this.statusPagamento = PagamentoStatus.RECUSADO;
        // Opcional: Atualizar status da OS para refletir a falha, se o fluxo exigir paralisacao
         this.status = Status.AGUARDANDO_PAGAMENTO; 
    }

    public void iniciarDiagnostico() {
        this.status = Status.EM_DIAGNOSTICO;
        this.dataInicioDiagnostico = LocalDateTime.now();
    }


    public void finalizarDiagnostico(List<ItemOrdemServico> itens) {
        this.status = Status.AGUARDANDO_APROVACAO;
        this.itens.clear();
        this.itens.addAll(itens);
        this.dataFimDiagnostico = LocalDateTime.now();
    }

    public void finalizar() {
        if (this.statusPagamento != PagamentoStatus.APROVADO) {
            throw new IllegalStateException("A ordem de serviço só pode ser finalizada se o pagamento estiver aprovado.");
        }
        this.status = Status.FINALIZADA;
        this.dataFinalizacao = LocalDateTime.now();
    }

    public void entregar() {
        this.status = Status.ENTREGUE;
        this.dataEntrega = LocalDateTime.now();
    }

    public void aprovar() {
        this.status = Status.AGUARDANDO_EXECUCAO;
    }

    public void vincularOrcamento(UUID orcamentoId) {
        this.orcamentoId = orcamentoId;
    }


    private BigDecimal calcularValorTotal() {
        if (this.itens.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return this.itens.stream()
                .map(ItemOrdemServico::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
