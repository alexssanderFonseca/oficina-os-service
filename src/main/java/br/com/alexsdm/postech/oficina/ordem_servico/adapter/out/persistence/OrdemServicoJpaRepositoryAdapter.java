package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.persistence;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.persistence.entity.OrdemServicoJpaRepository;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.persistence.entity.StatusEntity;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.persistence.mapper.OrdemServicoMapper;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.OrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.pagination.Page;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrdemServicoJpaRepositoryAdapter implements OrdemServicoRepository {

    private final OrdemServicoJpaRepository repository;
    private final OrdemServicoMapper mapper;

    @Override
    public OrdemServico salvar(OrdemServico ordemServico) {
        var entity = mapper.toEntity(ordemServico);
        var savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<OrdemServico> buscarPorId(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<OrdemServico> buscarPeloIdCliente(
            UUID clienteId) {

        var ordensServicoPage = repository.findByClienteId(clienteId);
        return ordensServicoPage.stream()
                .map(mapper::toDomain)
                .toList();


    }

    @Override
    public List<OrdemServico> buscarFinalizadas() {
        return repository.findByStatus(StatusEntity.FINALIZADA)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    public Page<OrdemServico> listarTodasOrdenadas(Long pagina, Long quantidade) {
        var page = repository.findAllOrdenadas(PageRequest.of(pagina.intValue(), quantidade.intValue()));
        var conteudo = page.map(mapper::toDomain).toList();
        return new Page<>(conteudo, page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber());

    }

}
