package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.ClienteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "cliente-service", url = "${feign.client.config.cliente-service.url}")
public interface ClienteFeignClient {

    @GetMapping("/clientes/{id}")
    ClienteResponse buscarPorId(@PathVariable("id") UUID id);
}
