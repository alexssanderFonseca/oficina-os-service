package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.persistence.entity;

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

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPecaOrdemServicoEntity> itensPecaOrdemServico;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemServicoOrdemServicoEntity> itensServico;

    @Enumerated(EnumType.STRING)
    private StatusEntity status;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataInicioDaExecucao;
    private LocalDateTime dataEntrega;
    private LocalDateTime dataFinalizacao;
    private LocalDateTime dataInicioDiagnostico;
    private LocalDateTime dataFimDiagnostico;
}
