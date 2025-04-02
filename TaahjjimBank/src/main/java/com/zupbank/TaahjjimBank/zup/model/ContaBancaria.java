package com.zupbank.TaahjjimBank.zup.model;

import java.math.BigDecimal;

public class ContaBancaria {
    private int agencia;
    private String numeroCC;
    private BigDecimal saldo;
    private String cpfProprietario;
    private TipoConta tipConta;

    public ContaBancaria(int agencia, String numeroCC, BigDecimal saldo, String cpfProprietario, TipoConta tipoConta) {
        this.agencia = agencia;
        this.numeroCC = numeroCC;
        this.saldo = saldo;
        this.cpfProprietario = cpfProprietario;
        this.tipConta = tipConta;
    }

    // Getters e Setters
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

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getCpfProprietario() {
        return cpfProprietario;
    }

    public void setCpfProprietario(String cpfProprietario) {
        this.cpfProprietario = cpfProprietario;
    }

    public TipoConta getTipoConta() {
        return tipConta;
    }

    public void setTipoConta(TipoConta tipoConta) {
        this.tipConta = tipConta;
    }
}
