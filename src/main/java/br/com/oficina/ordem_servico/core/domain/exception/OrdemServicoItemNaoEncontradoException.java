package br.com.oficina.ordem_servico.core.domain.exception;

import java.util.UUID;

public class OrdemServicoItemNaoEncontradoException extends OrdemServicoException {

    public OrdemServicoItemNaoEncontradoException(UUID idPecaInsumo) {
        super("Item peca/insumo não encontrado para o id solicitado: " + idPecaInsumo);
    }
}