package br.com.oficina.ordem_servico.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ordem_servico")
public class OrdemServicoEntity {

    @Id
    private UUID id;
    private UUID clienteId;
    private String clienteNomeCompleto;
    private String clienteCpfCnpj;
    private UUID veiculoId;
    private String veiculoPlaca;
    private String veiculoMarca;
    private String veiculoModelo;
    private String veiculoAno;
    private String veiculoCor;
    @Column(name = "orcamento_id")
    private UUID orcamentoId;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemOrdemServicoEntity> itens;

    @Enumerated(EnumType.STRING)
    private StatusEntity status;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataInicioDaExecucao;
    private LocalDateTime dataEntrega;
    private LocalDateTime dataFinalizacao;
    private LocalDateTime dataInicioDiagnostico;
    private LocalDateTime dataFimDiagnostico;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento")
    private PagamentoStatusEntity statusPagamento;

    @Column(name = "pagamento_id")
    private UUID pagamentoId;
}
