package br.com.oficina.ordem_servico.bdd;

import java.math.BigDecimal;
import java.util.UUID;

// Placeholder DTO
public record ItemDTO(UUID id, String nome, BigDecimal valor, String tipo) {
}
