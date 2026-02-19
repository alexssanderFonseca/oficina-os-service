package br.com.oficina.ordem_servico.adapter.out.persistence.mapper;

import br.com.oficina.ordem_servico.adapter.out.persistence.entity.ItemOrdemServicoEntity;
import br.com.oficina.ordem_servico.adapter.out.persistence.entity.TipoItemEntity;
import br.com.oficina.ordem_servico.core.domain.entity.ItemOrdemServico;
import br.com.oficina.ordem_servico.core.domain.entity.TipoItem;

public class ItemOrdemServicoMapper {

    private ItemOrdemServicoMapper(){}

    public static ItemOrdemServico toDomain(ItemOrdemServicoEntity entity) {
        return ItemOrdemServico.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .preco(entity.getPreco())
                .quantidade(entity.getQuantidade())
                .itemId(entity.getItemId())
                .tipo(TipoItemMapper.toDomain(entity.getTipo()))
                .build();
    }

    public static ItemOrdemServicoEntity toEntity(ItemOrdemServico domain) {
        var entity = new ItemOrdemServicoEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setDescricao(domain.getDescricao());
        entity.setPreco(domain.getPreco());
        entity.setQuantidade(domain.getQuantidade());
        entity.setItemId(domain.getItemId());
        entity.setTipo(TipoItemMapper.toEntity(domain.getTipo()));
        return entity;
    }

    static class TipoItemMapper {
        public static TipoItem toDomain(TipoItemEntity entity) {
            return TipoItem.valueOf(entity.name());
        }

        public static TipoItemEntity toEntity(TipoItem domain) {
            return TipoItemEntity.valueOf(domain.name());
        }
    }
}
