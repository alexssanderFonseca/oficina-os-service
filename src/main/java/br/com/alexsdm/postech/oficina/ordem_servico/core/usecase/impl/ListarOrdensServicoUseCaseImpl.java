package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.port.in.ListarOrdensServicoPeloIdClienteUseCase;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output.ListarOrdensServicoPeloIdClienteUseCaseOutput;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Named
@RequiredArgsConstructor
public class ListarOrdensServicoUseCaseImpl implements ListarOrdensServicoPeloIdClienteUseCase {

    private final OrdemServicoRepository repository;

    @Override
    public List<ListarOrdensServicoPeloIdClienteUseCaseOutput> executar(UUID idCliente) {

        return repository.buscarPeloIdCliente(idCliente)
                .stream()
                .map(ordemServico -> new ListarOrdensServicoPeloIdClienteUseCaseOutput(
                        ordemServico.getId(),
                        ordemServico.getStatus().name()
                ))
                .toList();


    }
}