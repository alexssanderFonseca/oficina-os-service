package br.com.oficina.ordem_servico.core.port.out;

import br.com.oficina.ordem_servico.core.domain.entity.Cliente;

import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoClientePort {
    Optional<Cliente> buscarCliente(UUID id);
}
