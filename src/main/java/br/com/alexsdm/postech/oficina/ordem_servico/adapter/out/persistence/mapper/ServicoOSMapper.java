package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.persistence.mapper;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.ItemServicoOrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.persistence.entity.ItemServicoOrdemServicoEntity;

import java.math.BigDecimal;

public class ServicoOSMapper {

    private ServicoOSMapper(){}

    public static ItemServicoOrdemServico toDomain(ItemServicoOrdemServicoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ItemServicoOrdemServico(
                entity.getId(),
                entity.getServicoId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getPrecoUnitario()
        );
    }

    public static ItemServicoOrdemServicoEntity toEntity(ItemServicoOrdemServico domain) {
        if (domain == null) {
            return null;
        }
        var entity = new ItemServicoOrdemServicoEntity();
        entity.setId(domain.getId());
        entity.setServicoId(domain.getServicoId());
        entity.setPrecoUnitario(domain.getPreco());
        entity.setNome(domain.getNome());
        entity.setDescricao(domain.getDescricao());
        entity.setPrecoUnitario(BigDecimal.ZERO);
        return entity;
    }
}
