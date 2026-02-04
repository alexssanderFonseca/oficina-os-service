package br.com.alexsdm.postech.oficina.ordem_servico.adapter.in.controller.mapper;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.in.controller.request.CriarOrdemDeServicoRequest;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.input.CriarOrdemServicoInput;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrdemServicoControllerMapper {
    CriarOrdemServicoInput toInput(CriarOrdemDeServicoRequest request);
}
