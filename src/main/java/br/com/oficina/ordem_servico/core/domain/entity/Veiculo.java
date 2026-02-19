package br.com.oficina.ordem_servico.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Veiculo {
    private UUID id;
    private String placa;
    private String marca;
    private String modelo;
    private String ano;
    private String cor;
}
