package model;

import util.Validation;

import javax.validation.constraints.*;

public class ContaBancariaModel {

    @NotNull(message = "Id é obrigatório")
    private String id;
    @NotNull(message = "Agencia é obrigatória")
    private int agencia;
    @NotNull(message = "Número Conta bancária é obrigatório")
    private String numeroCC;
    @NotNull(message = "Saldo é obrigatório")
    @Min(value = 0, message = "Saldo deve ser maior ou igual a zero")
    @Max(value = 1000000, message = "Saldo não pode exceder 1.000.000")
    private double saldo;
    @NotNull
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 dígitos")
    private String cpf;
    @NotNull(message = "Tipo Conta é obrigatório")
    private TipoConta tipoConta;

    public ContaBancariaModel(String id, int agencia, String numeroCC,
                              double saldo, String cpf, TipoConta tipoConta) {
        this.id = id;
        this.agencia = agencia;
        this.numeroCC = numeroCC;
        this.saldo = saldo;
        this.cpf = cpf;
        this.tipoConta = tipoConta;
        Validation.validar(this);
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

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
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
