package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//criando getters, setters e constructors
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class CartaoModel {

    private String id;

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public String getId() {
        return id;
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

    private String numeroCartao;

    private String validade;

    private String codigo;

    private String numeroConta;

    public CartaoModel(Object o, String numeroCartao, String validade, String codigo, String numeroConta) {
    }

    public void setId(String id) {
        this.id = id;
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
