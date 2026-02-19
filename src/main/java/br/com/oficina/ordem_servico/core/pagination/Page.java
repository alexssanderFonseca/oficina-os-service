package br.com.oficina.ordem_servico.core.pagination;

import java.util.List;

public record Page<T>(
        List<T> conteudo,
        long totalPaginas,
        long totalElementos,
        int pagina
) {

}
