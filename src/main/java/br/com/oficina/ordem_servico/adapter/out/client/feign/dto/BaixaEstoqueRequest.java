package br.com.oficina.ordem_servico.adapter.out.client.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaixaEstoqueRequest {
    private UUID itemId;
    private Integer quantidade;
}
