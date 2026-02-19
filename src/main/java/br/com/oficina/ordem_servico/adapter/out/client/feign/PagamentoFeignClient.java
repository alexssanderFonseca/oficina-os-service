package br.com.oficina.ordem_servico.adapter.out.client.feign;

import br.com.oficina.ordem_servico.adapter.out.client.feign.dto.PagamentoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "pagamento-service", url = "${feign.client.config.pagamento-service.url}")
public interface PagamentoFeignClient {

    @PostMapping("/pagamentos")
    void solicitarPagamento(@RequestBody PagamentoRequest request);
}
