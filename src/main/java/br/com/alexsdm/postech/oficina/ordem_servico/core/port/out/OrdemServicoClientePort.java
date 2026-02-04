package br.com.alexsdm.postech.oficina.ordem_servico.core.port.out;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Cliente;

import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoClientePort {
    Optional<Cliente> buscarCliente(UUID id);
}
