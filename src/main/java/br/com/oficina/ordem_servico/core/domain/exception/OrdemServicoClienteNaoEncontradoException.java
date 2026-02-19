package br.com.oficina.ordem_servico.core.domain.exception;

public class OrdemServicoClienteNaoEncontradoException extends OrdemServicoException {

    public OrdemServicoClienteNaoEncontradoException() {
        super("Cliente relacionado a ordem de serviço não encontrado para o id solicitado");
    }
}