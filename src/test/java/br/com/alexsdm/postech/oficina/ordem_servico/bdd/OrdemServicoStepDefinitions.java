package br.com.alexsdm.postech.oficina.ordem_servico.bdd;

import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.CatalogoFeignClient;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.ClienteFeignClient;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.OrcamentoFeignClient;
import br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto.*;
import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static io.restassured.RestAssured.given;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
public class OrdemServicoStepDefinitions {

    @LocalServerPort
    private int port;

    @MockitoBean
    private ClienteFeignClient clienteFeignClient;
    @MockitoBean
    private CatalogoFeignClient catalogoFeignClient;
    @MockitoBean
    private OrcamentoFeignClient orcamentoFeignClient;

    private final Map<String, Object> scenarioContext = new HashMap<>();

    private void setBaseURI() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Given("que um cliente e seu veiculo existem")
    public void que_um_cliente_e_seu_veiculo_existem() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        scenarioContext.put("clienteId", clienteId.toString());
        scenarioContext.put("veiculoId", veiculoId.toString());

        // Mock do veículo
        VeiculoResponse mockVeiculo = new VeiculoResponse(
                veiculoId,
                "BDD-1A23",
                "Volkswagen",
                "Golf",
                "2020",
                "Branco"
        );

        // Mock do cliente com lista de veículos
        ClienteResponse mockCliente = new ClienteResponse(
                clienteId,
                "João",
                "Silva",
                "123.456.789-00",
                List.of(mockVeiculo)
        );

        when(clienteFeignClient.buscarPorId(clienteId))
                .thenReturn(mockCliente);
    }

    @And("que itens de catalogo existem para o diagnostico")
    public void que_itens_de_catalogo_existem_para_o_diagnostico() {
        UUID pecaId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        scenarioContext.put("pecaId", pecaId.toString());
        scenarioContext.put("servicoId", servicoId.toString());

        // Mock da peça
        PecaInsumoResponse mockPeca = new PecaInsumoResponse(
                pecaId,
                "Filtro de Óleo BDD",
                "Filtro de óleo para motor",
                10,
                new BigDecimal("50.00")
        );

        // Mock do serviço
        ServicoResponse mockServico = new ServicoResponse(
                servicoId,
                "Alinhamento BDD",
                "Serviço de alinhamento e balanceamento",
                new BigDecimal("250.00")
        );

        when(catalogoFeignClient.buscarPecaPorId(pecaId))
                .thenReturn(mockPeca);
        when(catalogoFeignClient.buscarServicoPorId(servicoId))
                .thenReturn(mockServico);
    }

    @And("que o servico de orcamento esta funcionando")
    public void que_o_servico_de_orcamento_esta_funcionando() {
        UUID orcamentoId = UUID.randomUUID();
        scenarioContext.put("orcamentoId", orcamentoId);

        // Mock para criação de orçamento - retorna apenas o UUID
        when(orcamentoFeignClient.criar(any(CriarOrcamentoRequest.class)))
                .thenReturn(new CriarOrcamentoResponse(orcamentoId));

        // Mock para buscar orçamento por ID
        OrcamentoResponse mockOrcamento = new OrcamentoResponse(
                orcamentoId,
                "APROVADO"
        );

        when(orcamentoFeignClient.buscarPorId(orcamentoId))
                .thenReturn(mockOrcamento);
    }

    @When("eu abro uma nova ordem de serviço")
    public void eu_abro_uma_nova_ordem_de_servico() {
        setBaseURI();
        String clienteId = (String) scenarioContext.get("clienteId");
        String veiculoId = (String) scenarioContext.get("veiculoId");

        String requestBody = String.format("""
                {
                    "clienteId": "%s",
                    "veiculoId": "%s",
                    "descricaoProblema": "Veículo com barulho estranho na suspensão.",
                    "itens": []
                }
                """, clienteId, veiculoId);

        String osId = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/ordens-servicos")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        scenarioContext.put("osId", osId);
    }

    @And("eu inicio o diagnóstico da ordem de serviço")
    public void eu_inicio_o_diagnostico_da_ordem_de_servico() {
        String osId = (String) scenarioContext.get("osId");
        given()
                .when()
                .post("/ordens-servicos/" + osId + "/diagnosticos")
                .then()
                .statusCode(200);
    }

    @And("eu finalizo o diagnóstico informando os itens necessários")
    public void eu_finalizo_o_diagnostico_informando_os_itens_necessarios() {
        String osId = (String) scenarioContext.get("osId");
        String pecaId = (String) scenarioContext.get("pecaId");
        String servicoId = (String) scenarioContext.get("servicoId");

        String requestBody = String.format("""
                {
                    "itens": [
                        { "id": "%s", "quantidade": 1, "tipo": "PECA" },
                        { "id": "%s", "quantidade": 1, "tipo": "SERVICO" }
                    ]
                }
                """, pecaId, servicoId);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/ordens-servicos/" + osId + "/diagnosticos/finalizacoes")
                .then()
                .statusCode(200);
    }

    @And("eu aprovo o orçamento gerado")
    public void eu_aprovo_o_orcamento_gerado() {
        String osId = (String) scenarioContext.get("osId");

        given()
                .when()
                .post("/ordens-servicos/" + osId + "/aprovacoes")
                .then()
                .statusCode(200);
    }

    @And("eu inicio a execução da ordem de serviço")
    public void eu_inicio_a_execucao_da_ordem_de_servico() {
        String osId = (String) scenarioContext.get("osId");
        UUID orcamentoId = (UUID) scenarioContext.get("orcamentoId");

        String requestBody = String.format("""
                {
                    "orcamentoId": "%s"
                }
                """, orcamentoId);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/ordens-servicos/" + osId + "/execucoes")
                .then()
                .statusCode(204);
    }

    @And("eu finalizo a execução da ordem de serviço")
    public void eu_finalizo_a_execucao_da_ordem_de_servico() {
        String osId = (String) scenarioContext.get("osId");
        given()
                .when()
                .post("/ordens-servicos/" + osId + "/finalizacoes")
                .then()
                .statusCode(204);
    }

    @Then("eu registro a entrega do veículo ao cliente com sucesso")
    public void eu_registro_a_entrega_do_veiculo_ao_cliente_com_sucesso() {
        String osId = (String) scenarioContext.get("osId");
        given()
                .when()
                .post("/ordens-servicos/" + osId + "/entregas")
                .then()
                .statusCode(204);
    }
}