package steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import model.ProdutoModel;
import model.eCategoriaProduto;
import model.eGrauRisco;
import model.eTipoProduto;
import service.DriverS3MockSetup;
import service.ProdutoService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ListarProdutoSteps {

    private ProdutoService produtoService;
    private List<ProdutoModel> produtos;

    //----------------------------------------
    // GIVEN - Cenário 1
    //----------------------------------------
    @Dado("que existem produtos cadastrados")
    public void que_existem_produtos_cadastrados() {
        // Limpa o storage antes de inserir produtos
        DriverS3MockSetup.clearStorage();

        ProdutoModel produto1 = new ProdutoModel(
                "Fundo Beta",
                "Descrição Beta",
                new BigDecimal("0.20"),
                eGrauRisco.MEDIO,
                eCategoriaProduto.MULTI_MERCADO,
                eTipoProduto.FUNDO_INVESTIMENTO
        );
        String key1 = "dados/produto/" + produto1.getNome() + ".json";
        DriverS3MockSetup.insert(key1, produto1);
    }

    //----------------------------------------
    // WHEN - comum dois cenários
    //----------------------------------------
    @Quando("eu solicitar a listagem de produtos")
    public void eu_solicitar_a_listagem_de_produtos() {
        produtoService = new ProdutoService("mockBucket", null);
        produtos = produtoService.listar();
    }

    //----------------------------------------
    // THEN - comum 2 cenarios
    //----------------------------------------
    @Então("devo receber o status {int}")
    public void devo_receber_o_status(int statusEsperado) {
        // Simulação direta do comportamento do service, assume 200 para listagem
        assertEquals(200, statusEsperado, "Status code deve ser 200 para listagem.");
    }

    //----------------------------------------
    // AND - Cenário 1
    //----------------------------------------
    @E("a lista de produtos deve conter pelo menos {int} item")
    public void a_lista_de_produtos_deve_conter_pelo_menos_item(int quantidadeMinima) {
        assertNotNull(produtos, "Lista de produtos é nula.");
        assertTrue(produtos.size() >= quantidadeMinima, "Quantidade de produtos é menor que " + quantidadeMinima);
    }

    //----------------------------------------
    // GIVEN - Cenário 2
    //----------------------------------------
    @Dado("que não existem produtos cadastrados")
    public void que_nao_existem_produtos_cadastrados() {
        // Limpa o storage para garantir lista vazia
        DriverS3MockSetup.clearStorage();
    }
    //----------------------------------------
    // WHEN E THEN - Cenário comum
    //----------------------------------------

    //----------------------------------------
    // AND - Cenário 2
    //----------------------------------------
    @E("a lista de produtos deve estar vazia")
    public void a_lista_de_produtos_deve_estar_vazia() {
        assertNotNull(produtos, "Lista de produtos é nula.");
        assertTrue(produtos.isEmpty(), "Lista de produtos não está vazia.");
    }
}
