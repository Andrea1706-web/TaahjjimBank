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
import java.util.List;
import java.util.Map;

public class ProdutoSteps {

    private ProdutoService produtoService;
    private ProdutoModel produtoCriado;
    private String produtoJson;
    private List<ProdutoModel> produtos;

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

    @Quando("eu realizar uma requisição POST para {string}")
    public void eu_realizar_uma_requisicao_post_para(String endpoint) {
        // Inicia mock DriverS3
        DriverS3MockSetup.startMock();
        DriverS3MockSetup.clearStorage();

        produtoService = new ProdutoService("mockBucket", produtoJson);
        produtoCriado = produtoService.criar();
    }

    @Então("o serviço deve retornar o status {int}")
    public void o_serviço_deve_retornar_o_status(int status) {
        if (status == 201) {
            assertNotNull(produtoCriado, "Produto não foi criado.");
        } else if (status == 200) {
            if (produtos != null) {
                assertFalse(produtos.isEmpty(), "Lista de produtos está vazia.");
            } else {
                assertNotNull(produtoCriado, "Produto não encontrado.");
            }
        }
    }

    @Dado("que o produto {string} está cadastrado")
    public void que_o_produto_esta_cadastrado(String nome) {
        ProdutoModel produto = new ProdutoModel(
                nome,
                "Descrição padrão",
                new BigDecimal("0.10"),
                eGrauRisco.BAIXO,
                eCategoriaProduto.RENDA_FIXA,
                eTipoProduto.FUNDO_INVESTIMENTO
        );

        String key = "produto/" + nome;
        DriverS3MockSetup.insert(key, produto);
    }

    @Quando("eu realizar uma requisição GET para {string}")
    public void eu_realizar_uma_requisicao_get_para(String endpoint) {
        String nome = endpoint.replace("produto/", "");

        ProdutoService service = new ProdutoService("mockBucket", null);
        produtoCriado = service.obter(nome);

        if (produtoCriado == null) {
            throw new RuntimeException("Produto não encontrado");
        }
    }

    //@Entao: metodo comum na linha 61

    @E("a resposta deve conter os dados do produto {string}")
    public void a_resposta_deve_conter_os_dados_do_produto(String nomeEsperado) {
        assertNotNull(produtoCriado, "Produto não encontrado.");
        assertEquals(nomeEsperado, produtoCriado.getNome(), "Nome do produto não confere.");
    }


    @Dado("que existem produtos cadastrados")
    public void que_existem_produtos_cadastrados() {
        ProdutoModel produto1 = new ProdutoModel(
                "Fundo Beta",
                "Descrição Beta",
                new BigDecimal("0.20"),
                eGrauRisco.MEDIO,
                eCategoriaProduto.MULTI_MERCADO,
                eTipoProduto.FUNDO_INVESTIMENTO
        );
        String key1 = "produto/" + produto1.getNome();
        DriverS3MockSetup.insert(key1, produto1);
    }

    @Quando("realizar uma requisição GET para {string}")
    public void realizar_uma_requisicao_get_para(String endpoint) {
        ProdutoService service = new ProdutoService("mockBucket", null);

        if (endpoint.equals("produto")) {
            // Obter lista de produtos
            produtos = service.listar();
            if (produtos == null || produtos.isEmpty()) {
                throw new RuntimeException("Nenhum produto encontrado");
            }
        } else {
            // Requisição GET por nome
            String nome = endpoint.replace("produto/", "");
            produtoCriado = service.obter(nome);

            if (produtoCriado == null) {
                throw new RuntimeException("Produto não encontrado");
            }
        }
    }

     //@Entao: metodo comum na linha 61

    @E("a lista de produtos deve conter pelo menos {int} item")
    public void a_lista_de_produtos_deve_conter_pelo_menos_item(int quantidadeMinima) {
        assertNotNull(produtos, "Lista de produtos é nula.");
        assertTrue(produtos.size() >= quantidadeMinima, "Quantidade de produtos é menor que " + quantidadeMinima);
    }
}
