package steps;

import io.cucumber.java.pt.*;
import model.eCategoriaProduto;
import model.eGrauRisco;
import model.eTipoProduto;
import service.ProdutoService;
import service.DriverS3MockSetup;
import model.ProdutoModel;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Map;

public class CadastrarProdutoSteps {

    private ProdutoService produtoService;
    private ProdutoModel produtoCriado;
    private String produtoJson;

    // ----------------------------
    // Given - Cenário 1
    // ----------------------------
    @Dado("que eu tenho os dados do produto:")
    public void que_eu_tenho_os_dados_do_produto(io.cucumber.datatable.DataTable dataTable) throws Exception {
        Map<String, String> data = dataTable.asMaps(String.class, String.class).get(0);

        produtoJson = String.format("""
                        {
                          "nome": "%s",
                          "descricao": "%s",
                          "taxaAdministracao": %s,
                          "grauRisco": "%s",
                          "categoria": "%s",
                          "tipoProduto": "%s"
                        }
                        """,
                data.get("nome"),
                data.get("descricao"),
                data.get("taxaAdministracao"),
                data.get("grauRisco"),
                data.get("categoria"),
                data.get("tipoProduto")
        );
    }

    // ----------------------------
    // When - Cenário 1
    // ----------------------------
    @Quando("eu solicitar o cadastro do produto")
    public void eu_solicitar_o_cadastro_do_produto() {
        DriverS3MockSetup.startMock();
        produtoService = new ProdutoService("mockBucket", produtoJson);
    }

    // ----------------------------
    // Then - cadastro bem sucedido
    // ----------------------------
    @Então("o serviço de cadastro deve retornar o status 201 e o produto deve ser criado com sucesso")
    public void o_servico_de_cadastro_deve_retornar_status_201_e_produto_criado_com_sucesso() {
        produtoCriado = produtoService.criar();
        assertNotNull(produtoCriado, "Produto não foi criado.");
    }

    // ----------------------------
    // Given - Cenário 2
    // ----------------------------
    @Dado("que um produto está cadastrado no sistema com os seguintes dados:")
    public void um_produto_ja_esta_cadastrado(io.cucumber.datatable.DataTable dataTable) {
        DriverS3MockSetup.startMock();

        Map<String, String> data = dataTable.asMaps(String.class, String.class).get(0);

        ProdutoModel produto = new ProdutoModel(
                data.get("nome"),
                data.get("descricao"),
                new BigDecimal(data.get("taxaAdministracao")),
                eGrauRisco.valueOf(data.get("grauRisco")),
                eCategoriaProduto.valueOf(data.get("categoria")),
                eTipoProduto.valueOf(data.get("tipoProduto"))
        );

        String key = "dados/produto/" + data.get("nome") + ".json";
        DriverS3MockSetup.insert(key, produto);
    }

    // ----------------------------
    // When - Cenário 2
    // ----------------------------
    @Quando("eu solicitar o cadastro de um produto com o mesmo nome {string}")
    public void eu_solicitar_o_cadastro_de_um_produto_com_o_mesmo_nome(String nome) {
        // Montar o produtoJson com o nome passado para simular o produto duplicado
        produtoJson = String.format("""
                {
                  "nome": "%s",
                  "descricao": "Descrição duplicada",
                  "taxaAdministracao": 0.15,
                  "grauRisco": "BAIXO",
                  "categoria": "RENDA_FIXA",
                  "tipoProduto": "FUNDO_INVESTIMENTO"
                }
                """, nome);

        produtoService = new ProdutoService("mockBucket", produtoJson);
    }

    // ----------------------------
    // Then - erro de duplicidade
    // ----------------------------
    @Então("o serviço deve retornar um erro de duplicidade informando {string}")
    public void o_servico_deve_retornar_erro_de_duplicidade_informando(String expectedMessage) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            produtoService.criar();
        });

        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage),
                "Mensagem de erro esperada não encontrada. Recebido: " + actualMessage);
    }
}
