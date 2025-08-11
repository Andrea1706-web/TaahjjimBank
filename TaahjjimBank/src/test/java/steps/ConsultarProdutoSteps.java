package steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import model.ProdutoModel;
import model.enums.eCategoriaProduto;
import model.enums.eGrauRisco;
import model.enums.eTipoProduto;
import service.DriverS3MockSetup;
import service.ProdutoService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ConsultarProdutoSteps {

    private ProdutoService produtoService;
    private ProdutoModel produtoConsultado;
    private int statusCode;
    private String mensagemErro;

    // ----------------------------
    // Given - 1
    // ----------------------------
    @Dado("que o produto {string} existe no sistema")
    public void que_o_produto_existe_no_sistema(String nome) {
        ProdutoModel produto = new ProdutoModel(
                nome,
                "Descrição padrão",
                new BigDecimal("0.10"),
                eGrauRisco.BAIXO,
                eCategoriaProduto.RENDA_FIXA,
                eTipoProduto.FUNDO_INVESTIMENTO
        );
        String key = "dados/produto/" + nome + ".json";
        DriverS3MockSetup.insert(key, produto);
    }

    // ----------------------------
    // When - metodo comum cenario 1 e 2
    // ----------------------------
    @Quando("eu consultar o produto {string}")
    public void eu_consultar_o_produto(String nome) {
        produtoService = new ProdutoService("mockBucket", null);
        produtoConsultado = produtoService.obter(nome);

        if (produtoConsultado != null) {
            statusCode = 200;
        } else {
            statusCode = 404;
            mensagemErro = "Registro não encontrado";
        }
    }


    // ----------------------------
    // Then - metodo comum cenario 1 e 2
    // ----------------------------
    @Então("a resposta deve conter os dados do produto {string}")
    public void a_resposta_deve_conter_os_dados_do_produto(String nomeEsperado) {
        assertNotNull(produtoConsultado, "Produto não encontrado.");
        assertEquals(nomeEsperado, produtoConsultado.getNome(), "Nome do produto não confere.");
    }

    // ----------------------------
    // Given - cenario 2
    // ----------------------------
    @Dado("que o produto {string} não existe no sistema")
    public void que_o_produto_nao_existe_no_sistema(String nome) {
        String key = "dados/produto/" + nome + ".json";
        DriverS3MockSetup.clearStorage();
    }

    // ----------------------------
    // When - metodo comum cenario 1 e 2
    // ----------------------------

    // ----------------------------
    // Then
    // ----------------------------
    @Então("a resposta deve conter a mensagem {string}")
    public void a_resposta_deve_conter_a_mensagem(String mensagemEsperada) {
        assertEquals(mensagemEsperada, mensagemErro, "Registro não encontrado.");
    }
}
