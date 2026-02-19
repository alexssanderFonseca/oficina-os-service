package br.com.oficina.ordem_servico.core.domain.exception;

public class OrdemServicoException extends RuntimeException {
    public OrdemServicoException(String message) {
        super(message);
    }

    public OrdemServicoException(String message, Throwable cause) {
        super(message, cause);
    }
}