package br.com.oficina.ordem_servico.adapter.out.client.feign.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PagamentoRequest {
    private String external_reference;
    private Double amount;
    private String description;
}
