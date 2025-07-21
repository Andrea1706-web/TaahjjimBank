package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.util.UUID;

//criando getters, setters e constructors

public class CartaoModel {

    private final UUID id;

    @NotBlank(message = "Número de cartão é obrigatório")
    private String numeroCartao;

    @NotBlank(message = "Validade do cartão é obrigatório")
    private String validade;

    @NotBlank(message = "Número do código cartão é obrigatório")
    private String codigo;

    @NotBlank(message = "Número de conta é obrigatório")
    private String numeroConta;

    @NotNull(message = "O campo liberadoAproximacao é obrigatório")
    private boolean liberadoAproximacao;

    @NotNull(message = "O campo liberadoModoCredito é obrigatório")
    private boolean liberadoModoCredito;

    @JsonCreator
    public CartaoModel(
            @JsonProperty("numeroCartao") String numeroCartao,
            @JsonProperty("validade") String validade,
            @JsonProperty("codigo") String codigo,
            @JsonProperty("numeroConta") String numeroConta,
            @JsonProperty("liberadoAproximacao") boolean liberadoAproximacao,
            @JsonProperty("liberadoModoCredito") boolean liberadoModoCredito
    ) {
        this.id = UUID.randomUUID();
        this.numeroCartao = numeroCartao;
        this.validade = validade;
        this.codigo = codigo;
        this.numeroConta = numeroConta;
        this.liberadoAproximacao = liberadoAproximacao;
        this.liberadoModoCredito = liberadoModoCredito;
    }

    public UUID getId() {
        return id;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public String getValidade() {
        return validade;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public boolean isLiberadoAproximacao() {
        return liberadoAproximacao;
    }

    public void setLiberadoAproximacao(boolean liberadoAproximacao) {
        this.liberadoAproximacao = liberadoAproximacao;
    }

    public boolean isLiberadoModoCredito() {
        return liberadoModoCredito;
    }

    public void setLiberadoModoCredito(boolean liberadoModoCredito) {
        this.liberadoModoCredito = liberadoModoCredito;
    }
}