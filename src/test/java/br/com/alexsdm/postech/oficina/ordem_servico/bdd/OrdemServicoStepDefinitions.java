package br.com.alexsdm.postech.oficina.ordem_servico.bdd;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.CatalogoFeignClient;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.ClienteFeignClient;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.OrcamentoFeignClient;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.PagamentoFeignClient;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.*;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.OrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.PagamentoStatus;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.dto.events.PagamentoEventoDTO;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl.ProcessarRetornoPagamentoUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OrdemServicoStepDefinitions {

    @LocalServerPort
    private int port;

    @MockitoBean
    private ClienteFeignClient clienteFeignClient;
    @MockitoBean
    private CatalogoFeignClient catalogoFeignClient;
    @MockitoBean
    private OrcamentoFeignClient orcamentoFeignClient;
    @MockitoBean
    private PagamentoFeignClient pagamentoFeignClient;

    @Autowired
    private ProcessarRetornoPagamentoUseCase processarRetornoPagamentoUseCase;
    
    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    private final Map<String, Object> scenarioContext = new HashMap<>();
    private Response lastResponse;

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    private String getOsId() {
        Object id = scenarioContext.get("osId");
        if (id == null) throw new IllegalStateException("osId não encontrado no contexto");
        return id.toString();
    }

    @Given("que um cliente e seu veiculo existem")
    public void que_um_cliente_e_seu_veiculo_existem() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        scenarioContext.put("clienteId", clienteId.toString());
        scenarioContext.put("veiculoId", veiculoId.toString());

        VeiculoResponse mockVeiculo = new VeiculoResponse(
                veiculoId, "BDD-1A23", "Volkswagen", "Golf", "2020", "Branco"
        );

        ClienteResponse mockCliente = new ClienteResponse(
                clienteId, "João", "Silva", "123.456.789-00", List.of(mockVeiculo)
        );

        when(clienteFeignClient.buscarPorId(any(UUID.class))).thenReturn(mockCliente);
    }

    @And("que itens de catalogo existem para o diagnostico")
    public void que_itens_de_catalogo_existem_para_o_diagnostico() {
        UUID pecaId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        scenarioContext.put("pecaId", pecaId.toString());
        scenarioContext.put("servicoId", servicoId.toString());

        PecaInsumoResponse mockPeca = new PecaInsumoResponse(
                pecaId, "Filtro de Óleo BDD", "Filtro de óleo para motor", 10, new BigDecimal("50.00")
        );

        ServicoResponse mockServico = new ServicoResponse(
                servicoId, "Alinhamento BDD", "Serviço de alinhamento e balanceamento", new BigDecimal("250.00")
        );

        when(catalogoFeignClient.buscarPecaPorId(any(UUID.class))).thenReturn(mockPeca);
        when(catalogoFeignClient.buscarServicoPorId(any(UUID.class))).thenReturn(mockServico);
        doNothing().when(catalogoFeignClient).darBaixaEstoque(any());
        doNothing().when(catalogoFeignClient).reporEstoque(any());
    }

    @And("que o servico de orcamento esta funcionando")
    public void que_o_servico_de_orcamento_esta_funcionando() {
        UUID orcamentoId = UUID.randomUUID();
        scenarioContext.put("orcamentoId", orcamentoId);

        when(orcamentoFeignClient.criar(any(CriarOrcamentoRequest.class)))
                .thenReturn(new CriarOrcamentoResponse(orcamentoId));

        OrcamentoResponse mockOrcamento = new OrcamentoResponse(orcamentoId, "APROVADO");
        when(orcamentoFeignClient.buscarPorId(any(UUID.class))).thenReturn(mockOrcamento);
    }

    @And("que o servico de pagamento esta funcionando")
    public void que_o_servico_de_pagamento_esta_funcionando() {
        doNothing().when(pagamentoFeignClient).solicitarPagamento(any(PagamentoRequest.class));
    }

    @When("eu abro uma nova ordem de serviço")
    public void eu_abro_uma_nova_ordem_de_servico() {
        String clienteId = (String) scenarioContext.get("clienteId");
        String veiculoId = (String) scenarioContext.get("veiculoId");

        String requestBody = String.format("{\"clienteId\": \"%s\", \"veiculoId\": \"%s\", \"itens\": []}", clienteId, veiculoId);

        lastResponse = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/ordens-servicos");
        
        lastResponse.then().statusCode(201);
        String id = lastResponse.path("id");
        scenarioContext.put("osId", id);
    }

    @And("eu inicio o diagnóstico da ordem de serviço")
    public void eu_inicio_o_diagnostico_da_ordem_de_servico() {
        given().when().post("/ordens-servicos/" + getOsId() + "/diagnosticos").then().statusCode(200);
    }

    @And("eu finalizo o diagnóstico informando os itens necessários")
    public void eu_finalizo_o_diagnostico_informando_os_itens_necessarios() {
        String pecaId = (String) scenarioContext.get("pecaId");
        String servicoId = (String) scenarioContext.get("servicoId");

        String requestBody = String.format("{\"itens\": [{\"id\": \"%s\", \"quantidade\": 1, \"tipo\": \"PECA\"}, {\"id\": \"%s\", \"quantidade\": 1, \"tipo\": \"SERVICO\"}]}", pecaId, servicoId);

        given().contentType(ContentType.JSON).body(requestBody)
                .when().post("/ordens-servicos/" + getOsId() + "/diagnosticos/finalizacoes")
                .then().statusCode(200);
    }

    @And("eu aprovo o orçamento gerado")
    public void eu_aprovo_o_orcamento_gerado() {
        given().when().post("/ordens-servicos/" + getOsId() + "/aprovacoes").then().statusCode(200);
    }

    @And("eu inicio a execução da ordem de serviço")
    public void eu_inicio_a_execucao_da_ordem_de_servico() {
        UUID orcamentoId = (UUID) scenarioContext.get("orcamentoId");
        String requestBody = String.format("{\"orcamentoId\": \"%s\"}", orcamentoId);

        lastResponse = given().contentType(ContentType.JSON).body(requestBody)
                .when().post("/ordens-servicos/" + getOsId() + "/execucoes");
        
        lastResponse.then().statusCode(204);
    }

    @Then("o pagamento da ordem de serviço deve ser solicitado")
    public void o_pagamento_da_ordem_de_servico_deve_ser_solicitado() {
        verify(pagamentoFeignClient, times(1)).solicitarPagamento(any(PagamentoRequest.class));
    }

    @When("o pagamento da ordem de serviço e aprovado")
    public void o_pagamento_da_ordem_de_servico_e_aprovado() throws JsonProcessingException {
        UUID osId = UUID.fromString(getOsId());
        processarRetornoPagamentoUseCase.execute(new PagamentoEventoDTO(osId, "APROVADO", UUID.randomUUID()));
    }

    @When("o pagamento da ordem de serviço e recusado")
    public void o_pagamento_da_ordem_de_servico_e_recusado() throws JsonProcessingException {
        UUID osId = UUID.fromString(getOsId());
        processarRetornoPagamentoUseCase.execute(new PagamentoEventoDTO(osId, "RECUSADO", UUID.randomUUID()));
    }

    @Then("os itens de estoque devem ser repostos")
    public void os_itens_de_estoque_devem_ser_repostos() {
        verify(catalogoFeignClient, times(1)).reporEstoque(any());
    }

    @And("a ordem de servico deve ter o status de pagamento recusado")
    public void a_ordem_de_servico_deve_ter_o_status_de_pagamento_recusado() {
        OrdemServico os = ordemServicoRepository.buscarPorId(UUID.fromString(getOsId()))
                .orElseThrow(() -> new RuntimeException("OS não encontrada"));
        assertEquals(PagamentoStatus.RECUSADO, os.getStatusPagamento());
    }

    @And("o status de pagamento da ordem de serviço esta como {string}")
    public void o_status_de_pagamento_da_ordem_de_servico_esta_como(String statusPagamento) {
        OrdemServico os = ordemServicoRepository.buscarPorId(UUID.fromString(getOsId()))
                .orElseThrow(() -> new RuntimeException("OS não encontrada"));
        assertEquals(statusPagamento, os.getStatusPagamento().name());
    }

    @When("eu tento finalizar a execução da ordem de serviço")
    public void eu_tento_finalizar_a_execucao_da_ordem_de_servico() {
        lastResponse = given().when().post("/ordens-servicos/" + getOsId() + "/finalizacoes");
    }

    @Then("eu devo ver uma mensagem de erro {string}")
    public void eu_devo_ver_uma_mensagem_de_erro(String mensagemErro) {
        lastResponse.then().statusCode(400).body("message", containsString(mensagemErro));
    }

    @And("eu finalizo a execução da ordem de serviço")
    public void eu_finalizo_a_execucao_da_ordem_de_servico() {
        given().when().post("/ordens-servicos/" + getOsId() + "/finalizacoes").then().statusCode(204);
    }

    @Then("eu registro a entrega do veículo ao cliente com sucesso")
    public void eu_registro_a_entrega_do_veiculo_ao_cliente_com_sucesso() {
        given().when().post("/ordens-servicos/" + getOsId() + "/entregas").then().statusCode(204);
    }
}
