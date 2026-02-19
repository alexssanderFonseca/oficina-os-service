package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.dto.events;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoEventoDTO {
    private UUID externalReference; // Corresponde ao ID da Ordem de Serviço
    private String status; // "APROVADO" ou "RECUSADO"
    private UUID pagamentoId;
}
