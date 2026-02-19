package br.com.oficina.ordem_servico.adapter.in.controller;

import br.com.oficina.ordem_servico.adapter.in.controller.mapper.OrdemServicoControllerMapper;
import br.com.oficina.ordem_servico.adapter.in.controller.request.CriarOrdemDeServicoRequest;
import br.com.oficina.ordem_servico.adapter.in.controller.request.ExecutarOrdemServicoRequest;
import br.com.oficina.ordem_servico.adapter.in.controller.request.FinalizarDiagnosticoRequest;
import br.com.oficina.ordem_servico.core.port.in.*;
import br.com.oficina.ordem_servico.core.usecase.input.FinalizarDiagnosticoInput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/ordens-servicos")
@RequiredArgsConstructor
@Tag(name = "Ordens de Serviço", description = "Gerenciamento das ordens de serviço")
public class OrdemServicoController {

    private static final Logger logger = LoggerFactory.getLogger(OrdemServicoController.class);

    private final AbrirOrdemServicoUseCase abrirOrdemServicoUseCase;
    private final FinalizarDiagnosticoUseCase finalizarDiagnosticoUseCase;
    private final ExecutarOrdemServicoUseCase executarOrdemServicoUseCase;
    private final FinalizarOrdemServicoUseCase finalizarOrdemServicoUseCase;
    private final EntregarOrdemServicoUseCase entregarOrdemServicoUseCase;
    private final BuscarOrdemServicoPorIdUseCase buscarOrdemServicoPorIdUseCase;
    private final ListarOrdemServicoUsecase listarOrdemServicoUsecase;
    private final IniciarDiagnosticoOrdemServicoUsecase iniciarDiagnosticoOrdemServicoUsecase;
    private final AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase;
    private final OrdemServicoControllerMapper mapper;


    @Operation(summary = "Abre uma nova ordem de serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ordem de serviço aberta com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody @Valid CriarOrdemDeServicoRequest request) {
        logger.info("Recebida requisição para criar nova ordem de serviço.");
        var id = abrirOrdemServicoUseCase.executar(mapper.toInput(request));
        logger.info("Ordem de serviço ID {} criada com sucesso via controller.", id);
        return ResponseEntity.created(URI.create("/ordens-servicos/" + id)).body(new IdResponse(id));
    }

    @Operation(summary = "Aprova uma ordem de serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ordem de serviço aprovada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada")
    })
    @PostMapping("/{id}/aprovacoes")
    public ResponseEntity<?> aprovar(@PathVariable UUID id) {
        aprovarOrdemServicoUseCase.executar(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Finaliza o diagnóstico de uma OS, informando itens necessários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diagnóstico finalizado e orçamento gerado"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada")
    })
    @PostMapping("/{id}/diagnosticos/finalizacoes")
    public ResponseEntity<?> finalizarDiagnostico(@PathVariable UUID id,
                                                  @RequestBody @Valid FinalizarDiagnosticoRequest request) {

        var itensInput = request.itens()
                .stream()
                .map(item -> new FinalizarDiagnosticoInput.ItemOrdemServicoInput(item.id(), item.quantidade(), item.tipo()))
                .toList();

        var input = new FinalizarDiagnosticoInput(id, itensInput);
        var orcamento = finalizarDiagnosticoUseCase.executar(input);
        return ResponseEntity.ok(new IdResponse(orcamento.orcamentoId()));
    }

    @Operation(summary = "Inicia a execução de uma ordem de serviço após aprovação do orçamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Execução da OS iniciada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada"),
            @ApiResponse(responseCode = "409", description = "Orçamento não aprovado ou OS em estado incorreto")
    })
    @PostMapping("/{id}/execucoes")
    public ResponseEntity<?> executar(@PathVariable UUID id, @RequestBody ExecutarOrdemServicoRequest request) {
        executarOrdemServicoUseCase.executar(id, request.orcamentoId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Finaliza a execução de uma ordem de serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "OS finalizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada")
    })
    @PostMapping("/{id}/finalizacoes")
    public ResponseEntity<Void> finalizar(@PathVariable UUID id) {
        finalizarOrdemServicoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Registra a entrega do veículo ao cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Veículo entregue com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada")
    })
    @PostMapping("/{id}/entregas")
    public ResponseEntity<?> entregar(@PathVariable UUID id) {
        entregarOrdemServicoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca uma ordem de serviço por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ordem de serviço encontrada"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable(name = "id") UUID id) {
        var os = buscarOrdemServicoPorIdUseCase.executar(id);
        return ResponseEntity.ok(os);
    }

    @Operation(summary = "Lista todas as ordens de serviço de forma paginada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ordens de serviço retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<?> listar(@RequestParam(defaultValue = "0") Long pagina,
                                    @RequestParam(defaultValue = "10") Long quantidade) {
        var output = listarOrdemServicoUsecase.executar(pagina, quantidade);
        var data = Map.of("pagina", output.pagina(),
                "totalPaginas", output.totalPaginas(),
                "totalElementos", output.totalElementos(),
                "data", output.conteudo());
        return ResponseEntity.ok(data);
    }

    @PostMapping("/{osId}/diagnosticos")
    public ResponseEntity<?> listar(@PathVariable("osId") UUID osId) {
        iniciarDiagnosticoOrdemServicoUsecase.executar(osId);
        return ResponseEntity.ok().build();
    }

}