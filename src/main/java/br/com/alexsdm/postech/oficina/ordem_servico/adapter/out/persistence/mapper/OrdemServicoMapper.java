package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.persistence.mapper;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.persistence.entity.OrdemServicoEntity;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Cliente;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.OrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Veiculo;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrdemServicoMapper {


    public OrdemServico toDomain(OrdemServicoEntity entity) {
        return OrdemServico.from(
                entity.getId(),
                new Cliente(entity.getClienteId(), entity.getClienteNomeCompleto(), entity.getClienteCpfCnpj(), null),
                new Veiculo(entity.getVeiculoId(), entity.getVeiculoPlaca(), entity.getVeiculoMarca(), entity.getVeiculoModelo(), entity.getVeiculoAno(), entity.getVeiculoCor()),
                entity.getItens().stream()
                        .map(ItemOrdemServicoMapper::toDomain)
                        .collect(Collectors.toList()),
                StatusMapper.toDomain(entity.getStatus()),
                entity.getDataCriacao(),
                entity.getDataInicioDaExecucao(),
                entity.getDataInicioDiagnostico(),
                entity.getDataFimDiagnostico(),
                entity.getDataEntrega(),
                entity.getDataFinalizacao()
        );
    }

    public OrdemServicoEntity toEntity(OrdemServico domain) {
        var entity = new OrdemServicoEntity();
        entity.setId(domain.getId());
        entity.setClienteId(domain.getCliente().getId());
        entity.setClienteNomeCompleto(domain.getCliente().getNomeCompleto());
        entity.setClienteCpfCnpj(domain.getCliente().getCpfCnpj());
        entity.setVeiculoId(domain.getVeiculo().getId());
        entity.setVeiculoPlaca(domain.getVeiculo().getPlaca());
        entity.setVeiculoMarca(domain.getVeiculo().getMarca());
        entity.setVeiculoModelo(domain.getVeiculo().getModelo());
        entity.setVeiculoAno(domain.getVeiculo().getAno());
        entity.setVeiculoCor(domain.getVeiculo().getCor());
        entity.setStatus(StatusMapper.toEntity(domain.getStatus()));
        entity.setDataCriacao(domain.getDataCriacao());
        entity.setDataInicioDiagnostico(domain.getDataInicioDiagnostico());
        entity.setDataFimDiagnostico(domain.getDataFimDiagnostico());
        entity.setDataInicioDaExecucao(domain.getDataInicioDaExecucao());
        entity.setDataEntrega(domain.getDataEntrega());
        entity.setDataFinalizacao(domain.getDataFinalizacao());
        entity.setItens(
                domain.getItens().stream()
                        .map(ItemOrdemServicoMapper::toEntity)
                        .collect(Collectors.toList()));

        entity.getItens().forEach(item -> item.setOrdemServico(entity));

        return entity;
    }
}