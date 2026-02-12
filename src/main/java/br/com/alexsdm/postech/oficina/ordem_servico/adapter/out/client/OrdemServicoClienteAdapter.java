package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.ClienteFeignClient;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Cliente;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Veiculo;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoClientePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrdemServicoClienteAdapter implements OrdemServicoClientePort {

    private final ClienteFeignClient clienteFeignClient;

    @Override
    public Optional<Cliente> buscarCliente(UUID id) {
        try {
            var clienteResponse = clienteFeignClient.buscarPorId(id);
            var cliente = new Cliente(
                    clienteResponse.id(),
                    clienteResponse.nome() + " " + clienteResponse.sobrenome(),
                    clienteResponse.cpfCnpj(),
                    clienteResponse.veiculos().stream().map(veiculoResponse -> new Veiculo(
                            veiculoResponse.id(),
                            veiculoResponse.placa(),
                            veiculoResponse.marca(),
                            veiculoResponse.modelo(),
                            veiculoResponse.ano(),
                            veiculoResponse.cor()
                    )).toList());

            return Optional.of(cliente);
        } catch (Exception e) {
            // Log the exception, e.g., using a logger framework
            // logger.error("Error fetching client with id {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }
}
