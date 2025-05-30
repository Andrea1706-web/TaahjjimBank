package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public class ProdutoModel {

    @NotNull(message = "ID é obrigatório")
    private UUID id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotNull(message = "Taxa de administração é obrigatória")
    @DecimalMin(value = "0.0", inclusive = true, message = "Taxa de administração deve ser maior ou igual a 0")
    private BigDecimal taxaAdministracao;

    @NotNull(message = "Grau de risco é obrigatório")
    private eGrauRisco grauRisco;

    // Pode ser null, categoria é opcional
    private eCategoriaProduto categoria;

    @NotNull(message = "Tipo de produto é obrigatório")
    private eTipoProduto tipoProduto;

    @JsonCreator
    public ProdutoModel(
            @JsonProperty("id") UUID id,
            @JsonProperty("nome") String nome,
            @JsonProperty("descricao") String descricao,
            @JsonProperty("taxaAdministracao") BigDecimal taxaAdministracao,
            @JsonProperty("grauRisco") eGrauRisco grauRisco,
            @JsonProperty("categoria") eCategoriaProduto categoria,
            @JsonProperty("tipoProduto") eTipoProduto tipoProduto
    ) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.taxaAdministracao = taxaAdministracao;
        this.grauRisco = grauRisco;
        this.categoria = categoria;
        this.tipoProduto = tipoProduto;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getTaxaAdministracao() {
        return taxaAdministracao;
    }

    public void setTaxaAdministracao(BigDecimal taxaAdministracao) {
        this.taxaAdministracao = taxaAdministracao;
    }

    public eGrauRisco getGrauRisco() {
        return grauRisco;
    }

    public void setGrauRisco(eGrauRisco grauRisco) {
        this.grauRisco = grauRisco;
    }

    public eCategoriaProduto getCategoria() {
        return categoria;
    }

    public void setCategoria(eCategoriaProduto categoria) {
        this.categoria = categoria;
    }

    public eTipoProduto getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(eTipoProduto tipoProduto) {
        this.tipoProduto = tipoProduto;
    }
}
