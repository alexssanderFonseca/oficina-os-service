package br.com.oficina.ordem_servico.adapter.out.persistence.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrdemServicoJpaRepository extends JpaRepository<OrdemServicoEntity, UUID> {
    @Query("""
                SELECT o FROM OrdemServicoEntity o
                WHERE o.status = :status
            """)
    List<OrdemServicoEntity> findByStatus(@Param("status") StatusEntity status);

    List<OrdemServicoEntity> findByClienteId(UUID clienteId);

    @Query("""
                SELECT o FROM OrdemServicoEntity o
                WHERE o.status NOT IN ('ENTREGUE', 'FINALIZADA')
                ORDER BY 
                    CASE o.status
                        WHEN 'EM_EXECUCAO' THEN 1
                        WHEN 'AGUARDANDO_APROVACAO' THEN 2
                        WHEN 'ABERTA' THEN 3
                        WHEN 'RECEBIDA' THEN 4
                        ELSE 5
                    END,
                    o.dataCriacao ASC
            """)
    Page<OrdemServicoEntity> findAllOrdenadas(Pageable pageRequest);
}
