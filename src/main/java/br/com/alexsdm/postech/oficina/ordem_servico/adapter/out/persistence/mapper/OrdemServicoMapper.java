package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.persistence.mapper;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.persistence.entity.OrdemServicoEntity;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Cliente;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.OrdemServico;
import org.springframework.stereotype.Component;

@Component
public class OrdemServicoMapper {


    public OrdemServico toDomain(OrdemServicoEntity entity) {
        return OrdemServico.from(
                entity.getId(),
                new Cliente(entity.getClienteId(), entity.getClienteNomeCompleto(), entity.getClienteCpfCnpj(), null),
                entity.getVeiculoId(),
                entity.getItensPecaOrdemServico().stream()
                        .map(ItemPecaOrdemServicoMapper::toDomain)
                        .toList(),
                entity.getItensServico().stream()
                        .map(ServicoOSMapper::toDomain)
                        .toList(),
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
        entity.setVeiculoId(domain.getVeiculoId());
        entity.setStatus(StatusMapper.toEntity(domain.getStatus()));
        entity.setDataCriacao(domain.getDataCriacao());
        entity.setDataInicioDiagnostico(domain.getDataInicioDiagnostico());
        entity.setDataFimDiagnostico(domain.getDataFimDiagnostico());
        entity.setDataInicioDaExecucao(domain.getDataInicioDaExecucao());
        entity.setDataEntrega(domain.getDataEntrega());
        entity.setDataFinalizacao(domain.getDataFinalizacao());
        entity.setItensPecaOrdemServico(
                domain.getItensPecaOrdemServico().stream()
                        .map(ItemPecaOrdemServicoMapper::toEntity)
                        .toList());
        entity.setItensServico(
                domain.getServicos().stream()
                        .map(ServicoOSMapper::toEntity)
                        .toList());

        entity.getItensServico().
                forEach(itemServicoOrdemServico -> itemServicoOrdemServico.setOrdemServico(entity));

        entity.getItensPecaOrdemServico().
                forEach(itemPecaOrdemServico -> itemPecaOrdemServico.setOrdemServico(entity));

        return entity;
    }
}