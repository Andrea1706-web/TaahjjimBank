package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import model.enums.eTipoConta;

import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

public class ContaBancariaModel {

    private final UUID id;
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
    private eTipoConta tipoConta;

    @JsonCreator
    public ContaBancariaModel(
            @JsonProperty("agencia") int agencia,
            @JsonProperty("numeroCC") String numeroCC,
            @JsonProperty("saldo") double saldo,
            @JsonProperty("cpf") String cpf,
            @JsonProperty("tipoConta") eTipoConta tipoConta) {
        this.id = UUID.randomUUID();
        this.agencia = agencia;
        this.numeroCC = numeroCC;
        this.saldo = saldo;
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

    public eTipoConta getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(eTipoConta tipoConta) {
        this.tipoConta = tipoConta;
    }

    public boolean existeContaComId(UUID id, List<ContaBancariaModel> contas) {
        if (id == null || contas == null) return false;
        return contas.stream().anyMatch(conta -> conta.getId().equals(id));
    }
}
