package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.persistence.mapper;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.PagamentoStatus;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.persistence.entity.PagamentoStatusEntity;

public class PagamentoStatusMapper {

    public static PagamentoStatus toDomain(PagamentoStatusEntity entity) {
        if (entity == null) {
            return null;
        }
        return PagamentoStatus.valueOf(entity.name());
    }

    public static PagamentoStatusEntity toEntity(PagamentoStatus domain) {
        if (domain == null) {
            return null;
        }
        return PagamentoStatusEntity.valueOf(domain.name());
    }
}
