package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.PecaInsumoResponse;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.ServicoResponse;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.BaixaEstoqueRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "catalogo-service", url = "${feign.client.config.catalogo-service.url}")
public interface CatalogoFeignClient {

    @GetMapping("/pecas/{id}")
    PecaInsumoResponse buscarPecaPorId(@PathVariable("id") UUID id);

    @GetMapping("/servicos/{id}")
    ServicoResponse buscarServicoPorId(@PathVariable("id") UUID id);

    @PutMapping("/pecas/dar-baixa-estoque")
    void darBaixaEstoque(@RequestBody List<BaixaEstoqueRequest> requests);
}
