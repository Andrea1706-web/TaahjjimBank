package dto;

import java.util.UUID;

public class NotificacaoDTO {
    private UUID transacaoId;
    private String email;
    private String nomeUsuario;
    private double valor;
    private String numeroContaOrigem;
    private boolean sucesso;
    private String motivoErro;

    public UUID getTransacaoId() {
        return transacaoId;
    }

    public void setTransacaoId(UUID transacaoId) {
        this.transacaoId = transacaoId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getNumeroContaOrigem() {
        return numeroContaOrigem;
    }

    public void setNumeroContaOrigem(String numeroContaOrigem) {
        this.numeroContaOrigem = numeroContaOrigem;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMotivoErro() {
        return motivoErro;
    }

    public void setMotivoErro(String motivoErro) {
        this.motivoErro = motivoErro;
    }
}
