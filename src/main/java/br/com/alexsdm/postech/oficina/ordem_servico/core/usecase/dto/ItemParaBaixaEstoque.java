package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemParaBaixaEstoque {
    private UUID itemId;
    private Integer quantidade;
}
