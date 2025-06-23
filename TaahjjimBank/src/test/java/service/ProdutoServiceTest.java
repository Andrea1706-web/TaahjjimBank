package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.ProdutoModel;
import model.eCategoriaProduto;
import model.eGrauRisco;
import model.eTipoProduto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ProdutoServiceTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private ProdutoModel produto1;
    private ProdutoModel produto2;
    private List<ProdutoModel> listaProdutos;
    private String produtoValidoJson;

    // 🔧 Setup mock antes de cada teste
    @BeforeEach
    void setup() {
        DriverS3MockSetup.startMock();
        DriverS3MockSetup.clearStorage();

        produto1 = new ProdutoModel(
                "TesouroDireto",
                "Investimento em títulos públicos",
                new BigDecimal("0.05"),
                eGrauRisco.BAIXO,
                eCategoriaProduto.RENDA_FIXA,
                eTipoProduto.FUNDO_INVESTIMENTO
        );

        produto2 = new ProdutoModel(
                "CDBBancoX",
                "Certificado de Depósito Bancário",
                new BigDecimal("0.08"),
                eGrauRisco.MEDIO,
                eCategoriaProduto.RENDA_VARIAVEL,
                eTipoProduto.CREDITO_IMOBILIARIO
        );

        listaProdutos = List.of(produto1, produto2);
        produtoValidoJson = toJson(produto1);
    }

    // 🔥 Finaliza o mock após cada teste
    @AfterEach
    void teardown() {
        DriverS3MockSetup.stopMock();
    }

    private String toJson(ProdutoModel produto) {
        try {
            return mapper.writeValueAsString(produto);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao serializar JSON", e);
        }
    }

    @Test
    @DisplayName("Cria produto com sucesso quando dados são válidos")
    void criaProdutoComSucesso() {
        ProdutoService produtoService = new ProdutoService("mockBucket", produtoValidoJson);
        ProdutoModel resultado = produtoService.criar();

        assertNotNull(resultado);
        assertEquals("TesouroDireto", resultado.getNome());

        Optional<Object> salvo = DriverS3MockSetup.get("dados/produto/TesouroDireto.json");
        assertTrue(salvo.isPresent());
    }

    @Test
    @DisplayName("Lança exceção quando tenta criar produto com nome duplicado")
    void lançaExcecaoQuandoNomeDuplicado() {
        DriverS3MockSetup.insert("dados/produto/TesouroDireto.json", produto1);

        ProdutoModel produtoDuplicado = new ProdutoModel(
                "TesouroDireto",
                "Outro investimento com mesmo nome",
                new BigDecimal("0.10"),
                eGrauRisco.BAIXO,
                eCategoriaProduto.RENDA_FIXA,
                eTipoProduto.FUNDO_INVESTIMENTO
        );
        String jsonDuplicado = toJson(produtoDuplicado);

        ProdutoService produtoService = new ProdutoService("mockBucket", jsonDuplicado);

        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, produtoService::criar);
        assertEquals("Nome já existente: TesouroDireto", excecao.getMessage());
    }

    @Test
    @DisplayName("Lança exceção quando campos obrigatórios são nulos")
    void lançaExcecaoDeCamposObrigatoriosNulos() {
        String bodyJson = """
            {
                "nome": null,
                "descricao": null,
                "taxaAdministracao": null,
                "grauRisco": null,
                "categoria": null,
                "tipoProduto": null
            }
            """;

        ProdutoService produtoService = new ProdutoService("mockBucket", bodyJson);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, produtoService::criar);

        String msg = ex.getMessage().toLowerCase();

        assertTrue(
                msg.contains("nome") &&
                        msg.contains("descricao") &&
                        msg.contains("taxaadministracao") &&
                        msg.contains("graurisco") &&
                        msg.contains("tipoproduto") &&
                        msg.contains("obrigatório"),
                "Mensagem deve conter campos obrigatórios"
        );
    }

    @Test
    @DisplayName("Cria produto mesmo com categoria nula pois esse campo é opcional")
    void criaProdutoComCategoriaNula() {
        String bodyJson = """
            {
                "nome": "Tesouro Direto Categoria Nula",
                "descricao": "Outro investimento com descrição",
                "taxaAdministracao": "0.10",
                "grauRisco": "BAIXO",
                "categoria": null,
                "tipoProduto": "FUNDO_INVESTIMENTO"
            }
            """;

        ProdutoService produtoService = new ProdutoService("mockBucket", bodyJson);
        ProdutoModel resultado = produtoService.criar();

        assertEquals("Tesouro Direto Categoria Nula", resultado.getNome());

        Optional<Object> salvo = DriverS3MockSetup.get("dados/produto/Tesouro Direto Categoria Nula.json");
        assertTrue(salvo.isPresent());
    }

    @Test
    @DisplayName("Retorna produto quando nome existe")
    void retornaProdutoQuandoNomeExiste() {
        DriverS3MockSetup.insert("dados/produto/TesouroDireto.json", produto1);

        ProdutoService service = new ProdutoService("mockBucket", null);
        ProdutoModel resultado = service.obter("TesouroDireto");

        assertNotNull(resultado);
        assertEquals("TesouroDireto", resultado.getNome());
    }

    @Test
    @DisplayName("Retorna null quando produto não existe")
    void retornaNullQuandoProdutoNaoExiste() {
        ProdutoService service = new ProdutoService("mockBucket", null);
        ProdutoModel resultado = service.obter("ProdutoInexistente");

        assertNull(resultado);
    }

    @Test
    @DisplayName("Lista todos os produtos cadastrados")
    void listaTodosOsProdutos() {
        DriverS3MockSetup.insert("dados/produto/TesouroDireto.json", produto1);
        DriverS3MockSetup.insert("dados/produto/CDBBancoX.json", produto2);

        ProdutoService produtoService = new ProdutoService("mockBucket", null);
        List<ProdutoModel> resultado = produtoService.listar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(produto1));
        assertTrue(resultado.contains(produto2));
    }
}
