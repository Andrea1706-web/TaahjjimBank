package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class TransacaoModel {
    private UUID id;
    private UUID idContaOrigem;
    private UUID idContaDestino;
    private LocalDateTime dataTransacao;
    private LocalDateTime dataAgendamento;
    private double valorTransacao;
    private String tipoTransacao;
    private String localidade;
    private String dispositivo;
    private boolean ehFralde = false;

    public TransacaoModel(ContaBancariaModel idContaOrigem, ContaBancariaModel idContaDestino, LocalDateTime dataTransacao,
                          LocalDateTime dataAgendamento, double valorTransacao, String tipoTransacao,
                          String localidade, String dispositivo, boolean ehFralde) {
        this.id = UUID.randomUUID();
        this.idContaOrigem = idContaOrigem.getId();
        this.idContaDestino = idContaDestino.getId();
        this.dataTransacao = dataTransacao;
        this.dataAgendamento = dataAgendamento;
        this.valorTransacao = valorTransacao;
        this.tipoTransacao = tipoTransacao;
        this.localidade = localidade;
        this.dispositivo = dispositivo;
        this.ehFralde = ehFralde;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIdContaOrigem() {
        return idContaOrigem;
    }

    public void setIdContaOrigem(UUID idContaOrigem) {
        this.idContaOrigem = idContaOrigem;
    }

    public UUID getIdContaDestino() {
        return idContaDestino;
    }

    public void setIdContaDestino(UUID idContaDestino) {
        this.idContaDestino = idContaDestino;
    }

    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public LocalDateTime getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDateTime dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public double getValorTransacao() {
        return valorTransacao;
    }

    public void setValorTransacao(double valorTransacao) {
        this.valorTransacao = valorTransacao;
    }

    public String getTipoTransacao() {
        return tipoTransacao;
    }

    public void setTipoTransacao(String tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

    public boolean isEhFralde() {
        return ehFralde;
    }

    public void setEhFralde(boolean ehFralde) {
        this.ehFralde = ehFralde;
    }
}
