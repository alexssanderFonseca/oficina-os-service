package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.persistence.mapper;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.ItemPecaOrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.persistence.entity.ItemPecaOrdemServicoEntity;

public class ItemPecaOrdemServicoMapper {

    private ItemPecaOrdemServicoMapper() {
    }

    public static ItemPecaOrdemServico toDomain(ItemPecaOrdemServicoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ItemPecaOrdemServico(
                entity.getId(),
                entity.getPecaId(),
                entity.getNome(),
                entity.getPrecoUnitario(),
                entity.getDescricao(),
                entity.getQuantidade()
        );
    }

    public static ItemPecaOrdemServicoEntity toEntity(ItemPecaOrdemServico domain) {
        if (domain == null) {
            return null;
        }
        var entity = new ItemPecaOrdemServicoEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setQuantidade(domain.getQuantidade());
        entity.setPecaId(domain.getPecaId());
        entity.setPrecoUnitario(domain.getPreco());
        entity.setDescricao(domain.getDescricao());


        return entity;
    }
}
