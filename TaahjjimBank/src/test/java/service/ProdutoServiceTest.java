package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.ProdutoModel;
import model.eCategoriaProduto;
import model.eGrauRisco;
import model.eTipoProduto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProdutoServiceTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private ProdutoModel produto1;
    private ProdutoModel produto2;
    private List<ProdutoModel> listaProdutos;
    private String produtoValidoJson;

    // Converte ProdutoModel para JSON
    private String toJson(ProdutoModel produto) {
        try {
            return mapper.writeValueAsString(produto);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao serializar JSON", e);
        }
    }

    @BeforeEach
    public void setUp() {
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

    @Test
    @DisplayName("Cria produto com sucesso quando dados são válidos")
    public void criaProdutoComSucesso() {
        try (MockedConstruction<DriverS3> mocked = mockConstruction(DriverS3.class,
                (mock, context) -> when(mock.readAll("dados/produto/")).thenReturn(List.of()))) {

            ProdutoService produtoService = new ProdutoService("mockBucket", produtoValidoJson);
            ProdutoModel resultado = produtoService.criar();

            DriverS3 driverMock = mocked.constructed().get(0);
            verify(driverMock).save(eq("dados/produto/TesouroDireto.json"), any(ProdutoModel.class));
            assertEquals("TesouroDireto", resultado.getNome());
        }
    }

    @Test
    @DisplayName("Lança exceção quando tenta criar produto com nome duplicado")
    public void lançaExcecaoQuandoNomeDuplicado() {
        ProdutoModel produtoDuplicado = new ProdutoModel(
                "TesouroDireto",
                "Outro investimento com mesmo nome",
                new BigDecimal("0.10"),
                eGrauRisco.BAIXO,
                eCategoriaProduto.RENDA_FIXA,
                eTipoProduto.FUNDO_INVESTIMENTO
        );
        String jsonDuplicado = toJson(produtoDuplicado);

        try (MockedConstruction<DriverS3> mocked = mockConstruction(DriverS3.class,
                (mock, context) -> when(mock.readAll("dados/produto/")).thenReturn(List.of(produto1)))) {

            ProdutoService produtoService = new ProdutoService("mockBucket", jsonDuplicado);

            IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, produtoService::criar);
            assertEquals("Nome já existente: TesouroDireto", excecao.getMessage());

            DriverS3 driverMock = mocked.constructed().get(0);
            verify(driverMock, never()).save(anyString(), any(ProdutoModel.class));
        }
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

        try (MockedConstruction<DriverS3> mocked = mockConstruction(DriverS3.class,
                (mock, context) -> when(mock.readAll("dados/produto/")).thenReturn(List.of()))) {

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
                    "Mensagem deve conter indicações de campos obrigatórios violados"
            );

            DriverS3<?> driverMock = mocked.constructed().get(0);
            verify(driverMock, never()).save(anyString(), any());
        }
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

        try (MockedConstruction<DriverS3> mocked = mockConstruction(DriverS3.class,
                (mock, context) -> when(mock.readAll("dados/produto/")).thenReturn(List.of()))) {

            ProdutoService produtoService = new ProdutoService("mockBucket", bodyJson);
            ProdutoModel resultado = produtoService.criar();

            assertEquals("Tesouro Direto Categoria Nula", resultado.getNome());

            DriverS3 driverMock = mocked.constructed().get(0);
            verify(driverMock).save(eq("dados/produto/Tesouro Direto Categoria Nula.json"), any(ProdutoModel.class));
        }
    }

    @Test
    @DisplayName("Retorna produto quando nome existe")
    void retornaProdutoQuandoNomeExiste() {
        String nomeProduto = produto1.getNome();
        String keyEsperada = "dados/produto/" + nomeProduto + ".json";

        try (MockedConstruction<DriverS3> mocked = mockConstruction(DriverS3.class,
                (mock, context) -> when(mock.read(keyEsperada)).thenReturn(Optional.of(produto1)))) {

            ProdutoService service = new ProdutoService("mockBucket", null);
            ProdutoModel resultado = service.obter(nomeProduto);

            assertNotNull(resultado);
            assertEquals(nomeProduto, resultado.getNome());

            DriverS3<?> driverMock = mocked.constructed().get(0);
            verify(driverMock).read(keyEsperada);
        }
    }

    @Test
    @DisplayName("Retorna null quando produto não existe")
    void retornaNullQuandoProdutoNaoExiste() {
        String nomeProduto = "ProdutoInexistente";
        String keyEsperada = "dados/produto/" + nomeProduto + ".json";

        try (MockedConstruction<DriverS3> mocked = mockConstruction(DriverS3.class,
                (mock, context) -> when(mock.read(keyEsperada)).thenReturn(Optional.empty()))) {

            ProdutoService service = new ProdutoService("mockBucket", null);
            ProdutoModel resultado = service.obter(nomeProduto);

            assertNull(resultado);

            DriverS3<?> driverMock = mocked.constructed().get(0);
            verify(driverMock).read(keyEsperada);
        }
    }

    @Test
    @DisplayName("Lista todos os produtos cadastrados")
    void listaTodosOsProdutos() {
        try (MockedConstruction<DriverS3> mocked = mockConstruction(DriverS3.class,
                (mock, context) -> when(mock.readAll("dados/produto/")).thenReturn(listaProdutos))) {

            ProdutoService produtoService = new ProdutoService("mockBucket", null);
            List<ProdutoModel> resultado = produtoService.listar();

            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            assertTrue(resultado.contains(produto1));
            assertTrue(resultado.contains(produto2));

            DriverS3<?> driverMock = mocked.constructed().get(0);
            verify(driverMock).readAll("dados/produto/");
        }
    }
}
