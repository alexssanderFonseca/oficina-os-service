package br.com.oficina.ordem_servico.adapter.out.persistence.mapper;

import br.com.oficina.ordem_servico.core.domain.entity.Status;
import br.com.oficina.ordem_servico.adapter.out.persistence.entity.StatusEntity;

public class StatusMapper {

    public static Status toDomain(StatusEntity entity) {
        if (entity == null) {
            return null;
        }
        return Status.valueOf(entity.name());
    }

    public static StatusEntity toEntity(Status domain) {
        if (domain == null) {
            return null;
        }
        return StatusEntity.valueOf(domain.name());
    }
}
