package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.*;

import java.util.UUID;

//criando getters, setters e constructors

public class CartaoModel {

    private final UUID id;
    @NotNull(message = "Número de cartão é obrigatório")
    private String numeroCartao;
    @NotNull(message = "Validade do cartão é obrigatório")
    private String validade;
    @NotNull(message = "Número do código cartão é obrigatório")
    private String codigo;
    @NotNull(message = "Número de conta é obrigatório")
    private String numeroConta;

    public CartaoModel(String numeroCartao, String validade, String codigo, String numeroConta) {
        this.id = UUID.randomUUID();
        this.numeroCartao = numeroCartao;
        this.validade = validade;
        this.codigo = codigo;
        this.numeroConta = numeroConta;
    }

    @JsonCreator
    public UUID getId() {
        return id;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public String getValidade() {
        return validade;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNumeroConta() {
        return numeroConta;
    }
    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }
}
