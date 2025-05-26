package model;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    private GrauRisco grauRisco;

    // Pode ser null, categoria é opcional
    private CategoriaProduto categoria;

    @NotNull(message = "Tipo de produto é obrigatório")
    private TipoProduto tipoProduto;

    public ProdutoModel(UUID id, String nome, String descricao, BigDecimal taxaAdministracao, GrauRisco grauRisco, CategoriaProduto categoria, TipoProduto tipoProduto) {
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

    public GrauRisco getGrauRisco() {
        return grauRisco;
    }

    public void setGrauRisco(GrauRisco grauRisco) {
        this.grauRisco = grauRisco;
    }

    public CategoriaProduto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaProduto categoria) {
        this.categoria = categoria;
    }

    public TipoProduto getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(TipoProduto tipoProduto) {
        this.tipoProduto = tipoProduto;
    }
}
