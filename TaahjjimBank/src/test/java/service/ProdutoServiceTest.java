package service;

import model.ProdutoModel;
import model.enums.eCategoriaProduto;
import model.enums.eGrauRisco;
import model.enums.eTipoProduto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import service.interfaces.iDriverS3;
import util.Consts;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    private iDriverS3<ProdutoModel> driverMock;
    private ProdutoService produtoService;

    @BeforeEach
    void setup() {
        driverMock = mock(iDriverS3.class);
        produtoService = new ProdutoService(driverMock);
    }

    @Test
    void criar_deve_salvar_quando_nao_ha_duplicado() {
        // Arrange
        ProdutoModel novo = new ProdutoModel(
                "MeuProduto",
                "Descrição do produto",
                new BigDecimal("10.00"),
                eGrauRisco.BAIXO,
                eCategoriaProduto.RENDA_FIXA,
                eTipoProduto.CREDITO_IMOBILIARIO
        );

        // Simula lista vazia (sem duplicados)
        when(driverMock.readAll(Consts.PATH_PRODUTO)).thenReturn(List.of());

        // Act
        ProdutoModel criado = produtoService.criar(novo);

        // Assert
        assertNotNull(criado);
        assertEquals("MeuProduto", criado.getNome());

        // Verifica que save foi chamado com a key esperada: PATH_PRODUTO + nome + ".json"
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ProdutoModel> modelCaptor = ArgumentCaptor.forClass(ProdutoModel.class);
        verify(driverMock, times(1)).save(keyCaptor.capture(), modelCaptor.capture());

        String expectedKey = Consts.PATH_PRODUTO + "MeuProduto" + ".json";
        assertEquals(expectedKey, keyCaptor.getValue());
        assertEquals("MeuProduto", modelCaptor.getValue().getNome());
    }

    @Test
    void criar_deve_lancar_excecao_quando_duplicado() {
        // Arrange
        ProdutoModel novo = new ProdutoModel(
                "ProdutoDuplicado",
                "Descrição",
                new BigDecimal("5.00"),
                eGrauRisco.BAIXO,
                eCategoriaProduto.RENDA_FIXA,
                eTipoProduto.CREDITO_IMOBILIARIO
        );

        ProdutoModel existente = new ProdutoModel(
                "ProdutoDuplicado",
                "Descrição existente",
                new BigDecimal("5.00"),
                eGrauRisco.BAIXO,
                eCategoriaProduto.RENDA_FIXA,
                eTipoProduto.CREDITO_IMOBILIARIO
        );

        when(driverMock.readAll(Consts.PATH_PRODUTO)).thenReturn(List.of(existente));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> produtoService.criar(novo));
        assertTrue(ex.getMessage().toLowerCase().contains("duplic"));

        // Nenhum save deve ter ocorrido
        verify(driverMock, never()).save(anyString(), any());
    }
}
