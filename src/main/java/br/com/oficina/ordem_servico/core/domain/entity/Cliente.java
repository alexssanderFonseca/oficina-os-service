package br.com.oficina.ordem_servico.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class Cliente {
    private UUID id;
    private String nomeCompleto;
    private String cpfCnpj;
    private List<Veiculo> veiculos;

    public Cliente(UUID id, String cpfCnpj) {
        this.id = id;
        this.cpfCnpj = cpfCnpj;
    }
}
