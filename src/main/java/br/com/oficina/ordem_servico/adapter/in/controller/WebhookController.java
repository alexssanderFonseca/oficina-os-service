package br.com.oficina.ordem_servico.adapter.in.controller;

import br.com.oficina.ordem_servico.adapter.in.controller.request.OrcamentoAprovadoWebhookRequest;
import br.com.oficina.ordem_servico.core.port.in.AprovarOrdemServicoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks/orcamento")
@RequiredArgsConstructor
@Tag(name = "Webhooks - Orçamento", description = "Endpoints para comunicação via webhook do serviço de orçamento")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
    private final AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase;

    @Operation(summary = "Recebe notificação de aprovação de orçamento via webhook")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificação de orçamento aprovado processada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada")
    })
    @PostMapping("/aprovacao")
    public ResponseEntity<Void> receberAprovacaoOrcamento(@RequestBody @Valid OrcamentoAprovadoWebhookRequest request) {
        logger.info("Webhook: Recebida notificação de orçamento aprovado para Orçamento ID: {} e Ordem de Serviço ID: {}",
                request.orcamentoId(), request.ordemServicoId());

        aprovarOrdemServicoUseCase.executar(request.ordemServicoId());

        logger.info("Webhook: Ordem de Serviço ID {} aprovada com sucesso via webhook.", request.ordemServicoId());
        return ResponseEntity.ok().build();
    }
}