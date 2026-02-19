package br.com.oficina.ordem_servico.adapter.out.persistence;

import br.com.oficina.ordem_servico.adapter.out.persistence.entity.OrdemServicoJpaRepository;
import br.com.oficina.ordem_servico.adapter.out.persistence.entity.StatusEntity;
import br.com.oficina.ordem_servico.adapter.out.persistence.mapper.OrdemServicoMapper;
import br.com.oficina.ordem_servico.core.domain.entity.OrdemServico;
import br.com.oficina.ordem_servico.core.pagination.Page;
import br.com.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrdemServicoJpaRepositoryAdapter implements OrdemServicoRepository {

    private static final Logger logger = LoggerFactory.getLogger(OrdemServicoJpaRepositoryAdapter.class);

    private final OrdemServicoJpaRepository repository;
    private final OrdemServicoMapper mapper;

    @Override
    public OrdemServico salvar(OrdemServico ordemServico) {
        logger.info("Salvando Ordem de Serviço com ID: {}", ordemServico.getId());
        try {
            var entity = mapper.toEntity(ordemServico);
            var savedEntity = repository.save(entity);
            logger.info("Ordem de Serviço ID {} salva com sucesso.", savedEntity.getId());
            return mapper.toDomain(savedEntity);
        } catch (Exception e) {
            logger.error("Erro ao salvar Ordem de Serviço com ID {}: {}", ordemServico.getId(), e.getMessage(), e);
            throw new RuntimeException("Erro ao salvar Ordem de Serviço.", e);
        }
    }

    @Override
    public Optional<OrdemServico> buscarPorId(UUID id) {
        logger.info("Buscando Ordem de Serviço por ID: {}", id);
        try {
            Optional<OrdemServico> result = repository.findById(id).map(mapper::toDomain);
            if (result.isPresent()) {
                logger.info("Ordem de Serviço com ID {} encontrada.", id);
            } else {
                logger.info("Ordem de Serviço com ID {} não encontrada.", id);
            }
            return result;
        } catch (Exception e) {
            logger.error("Erro ao buscar Ordem de Serviço por ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar Ordem de Serviço por ID.", e);
        }
    }

    @Override
    public List<OrdemServico> buscarPeloIdCliente(
            UUID clienteId) {
        logger.info("Buscando Ordens de Serviço para Cliente ID: {}", clienteId);
        try {
            var ordensServicoPage = repository.findByClienteId(clienteId);
            List<OrdemServico> result = ordensServicoPage.stream()
                    .map(mapper::toDomain)
                    .toList();
            logger.info("{} Ordens de Serviço encontradas para Cliente ID {}.", result.size(), clienteId);
            return result;
        } catch (Exception e) {
            logger.error("Erro ao buscar Ordens de Serviço para Cliente ID {}: {}", clienteId, e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar Ordens de Serviço para Cliente.", e);
        }
    }

    @Override
    public List<OrdemServico> buscarFinalizadas() {
        logger.info("Buscando Ordens de Serviço finalizadas.");
        try {
            List<OrdemServico> result = repository.findByStatus(StatusEntity.FINALIZADA)
                    .stream()
                    .map(mapper::toDomain)
                    .toList();
            logger.info("{} Ordens de Serviço finalizadas encontradas.", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Erro ao buscar Ordens de Serviço finalizadas: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar Ordens de Serviço finalizadas.", e);
        }
    }

    public Page<OrdemServico> listarTodasOrdenadas(Long pagina, Long quantidade) {
        logger.info("Listando todas as Ordens de Serviço paginadas. Página: {}, Quantidade: {}", pagina, quantidade);
        try {
            var page = repository.findAllOrdenadas(PageRequest.of(pagina.intValue(), quantidade.intValue()));
            var conteudo = page.map(mapper::toDomain).toList();
            logger.info("Listagem paginada de Ordens de Serviço concluída. Total de elementos: {}", page.getTotalElements());
            return new Page<>(conteudo, page.getTotalPages(),
                    page.getTotalElements(),
                    page.getNumber());
        } catch (Exception e) {
            logger.error("Erro ao listar todas as Ordens de Serviço paginadas (Página: {}, Quantidade: {}): {}", pagina, quantidade, e.getMessage(), e);
            throw new RuntimeException("Erro ao listar todas as Ordens de Serviço paginadas.", e);
        }
    }

}
