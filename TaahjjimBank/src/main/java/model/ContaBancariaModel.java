package model;

public class ContaBancariaModel {

    private String id;
    private int agencia;
    private String numeroCC;
    private float saldo;
    private String cpf;
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
