package br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter

public class OrdemServico {
    private UUID id;
    private Cliente cliente;
    private UUID veiculoId;
    private List<ItemPecaOrdemServico> itensPecaOrdemServico = new ArrayList<>();
    private List<ItemServicoOrdemServico> servicos = new ArrayList<>();
    private Status status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataInicioDiagnostico;
    private LocalDateTime dataFimDiagnostico;
    private LocalDateTime dataInicioDaExecucao;
    private LocalDateTime dataEntrega;
    private LocalDateTime dataFinalizacao;

    private OrdemServico(UUID id,
                         Cliente cliente,
                         UUID veiculoId,
                         List<ItemPecaOrdemServico> itensPecaOrdemServico,
                         List<ItemServicoOrdemServico> servicos,
                         Status status,
                         LocalDateTime dataCriacao,
                         LocalDateTime dataInicioDiagnostico,
                         LocalDateTime dataFimDiagnostico,
                         LocalDateTime dataInicioDaExecucao,
                         LocalDateTime dataEntrega,
                         LocalDateTime dataFinalizacao) {
        this.id = id;
        this.cliente = cliente;
        this.veiculoId = veiculoId;
        if (itensPecaOrdemServico != null) {
            this.itensPecaOrdemServico.addAll(itensPecaOrdemServico);
        }
        if (servicos != null) {
            this.servicos.addAll(servicos);
        }
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.dataInicioDaExecucao = dataInicioDaExecucao;
        this.dataInicioDiagnostico = dataInicioDiagnostico;
        this.dataFimDiagnostico = dataFimDiagnostico;
        this.dataEntrega = dataEntrega;
        this.dataFinalizacao = dataFinalizacao;
    }


    public static OrdemServico criarOrdemServicoParaDiagnostico(Cliente cliente,
                                                                UUID veiculoId) {
        return new OrdemServico(
                UUID.randomUUID(),
                cliente,
                veiculoId,
                Collections.emptyList(),
                Collections.emptyList(),
                Status.EM_DIAGNOSTICO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null,
                null,
                null
        );
    }

    public static OrdemServico criarOrdemServicoParaExecucao(Cliente cliente,
                                                             UUID veiculoId,
                                                             List<ItemPecaOrdemServico> itensPecaOrdemServico,
                                                             List<ItemServicoOrdemServico> servicos
    ) {
        return new OrdemServico(
                UUID.randomUUID(),
                cliente,
                veiculoId,
                itensPecaOrdemServico,
                servicos,
                Status.EM_EXECUCAO,
                LocalDateTime.now(),
                null,
                null,
                LocalDateTime.now(),
                null,
                null
        );
    }

    public static OrdemServico from(UUID id,
                                    Cliente cliente,
                                    UUID veiculoId,
                                    List<ItemPecaOrdemServico> itensPecaOrdemServico,
                                    List<ItemServicoOrdemServico> servicos,
                                    Status status,
                                    LocalDateTime dataCriacao,
                                    LocalDateTime dataInicioDaExecucao,
                                    LocalDateTime dataInicioDiagnostico,
                                    LocalDateTime dataFimDiagnostico,
                                    LocalDateTime dataEntrega,
                                    LocalDateTime dataFinalizacao) {
        return new OrdemServico(
                id,
                cliente,
                veiculoId,
                itensPecaOrdemServico,
                servicos,
                status,
                dataCriacao,
                dataInicioDiagnostico,
                dataFimDiagnostico,
                dataInicioDaExecucao,
                dataEntrega,
                dataFinalizacao);

    }


    public void adicionarPecasInsumos(List<ItemPecaOrdemServico> itens) {
        this.itensPecaOrdemServico.addAll(itens);
    }

    public void adicionarServicos(List<ItemServicoOrdemServico> itens) {
        this.servicos.addAll(itens);
    }

    public void executar(List<ItemPecaOrdemServico> itens,
                         List<ItemServicoOrdemServico> servicos) {
        this.status = Status.EM_EXECUCAO;
        this.adicionarServicos(servicos);
        this.adicionarPecasInsumos(itens);
        this.dataInicioDaExecucao = LocalDateTime.now();
    }

    public void iniciarDiagnostico() {
        this.status = Status.EM_DIAGNOSTICO;
        this.dataInicioDiagnostico = LocalDateTime.now();
    }


    public void finalizarDiagnostico() {
        this.status = Status.AGUARDANDO_APROVACAO;
        this.dataFimDiagnostico = LocalDateTime.now();
    }

    public void finalizar() {
        this.status = Status.FINALIZADA;
        this.dataFinalizacao = LocalDateTime.now();
    }

    public void entregar() {
        this.status = Status.ENTREGUE;
        this.dataEntrega = LocalDateTime.now();
    }
}
