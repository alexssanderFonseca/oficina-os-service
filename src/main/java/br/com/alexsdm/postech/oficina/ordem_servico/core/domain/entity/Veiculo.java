package br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Veiculo {
    private String id;
    private String placa;
    private String marca;
    private String modelo;
    private String ano;
    private String cor;
}
