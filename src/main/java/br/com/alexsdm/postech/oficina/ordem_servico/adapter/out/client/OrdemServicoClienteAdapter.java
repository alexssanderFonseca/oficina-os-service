package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.ClienteFeignClient;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Cliente;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Veiculo;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoClientePort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrdemServicoClienteAdapter implements OrdemServicoClientePort {

    private static final Logger logger = LoggerFactory.getLogger(OrdemServicoClienteAdapter.class);
    private final ClienteFeignClient clienteFeignClient;

    @Override
    public Optional<Cliente> buscarCliente(UUID id) {
        logger.info("Tentando buscar cliente com ID: {}", id);
        try {
            var clienteResponse = clienteFeignClient.buscarPorId(id);
            if (clienteResponse == null) {
                logger.warn("Cliente com ID {} não encontrado no serviço externo.", id);
                return Optional.empty();
            }
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
            logger.info("Cliente com ID {} buscado com sucesso.", id);
            return Optional.of(cliente);
        } catch (Exception e) {
            logger.error("Erro ao buscar cliente com ID {}: {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
