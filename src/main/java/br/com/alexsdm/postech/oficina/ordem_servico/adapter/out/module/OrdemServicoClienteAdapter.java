package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.module;

import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoClientePort;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Cliente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrdemServicoClienteAdapter implements OrdemServicoClientePort {

    //private final BuscarClientePorIdUseCase buscarClientePorIdUseCase;

    @Override
    public Optional<Cliente> buscarCliente(UUID id) {
//        var buscarClienteOutput = buscarClientePorIdUseCase.executar(id);
//        var cliente = new Cliente(
//                buscarClienteOutput.id(),
//                buscarClienteOutput.nome() + " " + buscarClienteOutput + buscarClienteOutput.sobrenome(),
//                buscarClienteOutput.cpfCnpj(),
//                buscarClienteOutput.veiculos().stream().map(veiculoOutput -> new Veiculo(
//                        veiculoOutput.id(),
//                        veiculoOutput.placa(),
//                        veiculoOutput.marca(),
//                        veiculoOutput.modelo(),
//                        veiculoOutput.ano(),
//                        veiculoOutput.cor()
//                )).toList()
//        );
//        return Optional.of(cliente);

        return null;
    }
}
