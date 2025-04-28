package model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;
import javax.persistence.Column;

public class ContaBancariaModel {

    @Column(unique = true)
    @NotNull(message = "O id é obrigatório")
    @NotBlank(message = "O id não pode estar vazio")
    private String id;
    @NotNull(message = "O campo 'agencia' é obrigatório")
    private int agencia;
    @Column(unique = true)
    @NotNull(message = "O campo 'numeroCC' é obrigatório")
    @NotBlank(message = "O campo 'numeroCC' não pode estar vazio")
    private String numeroCC;
    @NotNull(message = "o campo 'saldo' é obrigatório")
    private float saldo;
    @Column(unique = true)
    @NotNull(message = "O campo 'cpf' é obrigatório")
    @NotBlank(message = "O campo 'cpf' não pode estar vazio")
    @CPF(message = "O CPF deve ser válido")
    private String cpf;
    @NotNull(message = "O campo 'tipoConta' é obrigatório")
    private TipoConta tipoConta;

    public ContaBancariaModel(String id, int agencia, String numeroCC,
                              float saldo, String cpf, TipoConta tipoConta) {
        this.id = id;
        this.agencia = agencia;
        this.numeroCC = numeroCC;
        this.saldo = saldo;
        this.cpf = cpf;
        this.tipoConta = tipoConta;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAgencia() {
        return agencia;
    }

    public void setAgencia(int agencia) {
        this.agencia = agencia;
    }

    public String getNumeroCC() {
        return numeroCC;
    }

    public void setNumeroCC(String numeroCC) {
        this.numeroCC = numeroCC;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public TipoConta getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(TipoConta tipoConta) {
        this.tipoConta = tipoConta;
    }
}
