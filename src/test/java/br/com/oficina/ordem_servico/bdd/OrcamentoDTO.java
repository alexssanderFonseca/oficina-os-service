package br.com.oficina.ordem_servico.bdd;

import java.math.BigDecimal;
import java.util.UUID;

// Placeholder DTO
public record OrcamentoDTO(UUID id, String osId, String status, BigDecimal valor) {
}
