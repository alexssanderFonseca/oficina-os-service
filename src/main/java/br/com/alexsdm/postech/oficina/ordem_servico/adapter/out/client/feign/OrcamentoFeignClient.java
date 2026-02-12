package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.CriarOrcamentoRequest;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.OrcamentoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "orcamento-service", url = "${feign.client.config.orcamento-service.url}")
public interface OrcamentoFeignClient {

    @GetMapping("/orcamentos/{id}")
    OrcamentoResponse buscarPorId(@PathVariable("id") UUID id);

    @PostMapping("/orcamentos")
    UUID criar(@RequestBody CriarOrcamentoRequest request);
}
