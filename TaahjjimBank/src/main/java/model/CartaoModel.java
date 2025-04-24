package model;

import lombok.Getter;
import lombok.Setter;

//criando getters, setters
@Getter
@Setter

public class CartaoModel {
    private String id;

    private String numeroCartao;

    private String validade;

    private String codigo;

    private String numeroConta;

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public CartaoModel(String id, String numeroCartao, String validade, String codigo, String numeroConta) {
        this.id = id;
        this.numeroCartao = numeroCartao;
        this.validade = validade;
        this.codigo = codigo;
        this.numeroConta = numeroConta;
    }
}
