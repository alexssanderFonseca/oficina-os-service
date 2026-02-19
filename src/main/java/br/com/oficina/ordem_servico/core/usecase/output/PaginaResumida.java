package br.com.oficina.ordem_servico.core.usecase.output;

import java.util.List;

public record PaginaResumida<T>(
        List<T> content,
        long totalElements,
        long page
) {
}