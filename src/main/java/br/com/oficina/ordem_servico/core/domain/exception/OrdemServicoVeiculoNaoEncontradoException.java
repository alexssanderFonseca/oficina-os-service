package br.com.oficina.ordem_servico.core.domain.exception;

public class OrdemServicoVeiculoNaoEncontradoException extends OrdemServicoException {

    public OrdemServicoVeiculoNaoEncontradoException() {
        super("Veiculo relacionado a ordem de serviço não encontrado para o id solicitado");
    }
}