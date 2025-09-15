package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import model.enums.eTipoConta;

import jakarta.validation.constraints.*;

import java.util.UUID;

public class ContaBancariaModel {

    private final UUID id;
    private int agencia;
    private int numeroCC;
    private double saldo;
    @NotNull
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 dígitos")
    private String cpf;
    @NotNull(message = "Tipo Conta é obrigatório")
    private eTipoConta tipoConta;

    @JsonCreator
    public ContaBancariaModel(
            @JsonProperty("cpf") String cpf,
            @JsonProperty("tipoConta") eTipoConta tipoConta) {
        this.id = UUID.randomUUID();
        this.agencia = 0075;
        this.numeroCC = (int)(Math.random() * 90000) + 10000;
        this.saldo = 0;
        this.cpf = cpf;
        this.tipoConta = tipoConta;
    }

    public UUID getId() {
        return id;
    }

    public int getAgencia() {
        return agencia;
    }

    public void setAgencia(int agencia) {
        this.agencia = agencia;
    }

    public int getNumeroCC() {
        return numeroCC;
    }

    public void setNumeroCC(int numeroCC) {
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

    public eTipoConta getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(eTipoConta tipoConta) {
        this.tipoConta = tipoConta;
    }

}
