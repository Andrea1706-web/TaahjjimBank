package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import util.ContaExistente;

import java.time.LocalDateTime;
import java.util.UUID;

public class TransacaoModel {
    private UUID id;
    @ContaExistente
    private UUID idContaOrigem;
    @ContaExistente
    private UUID idContaDestino;
    private LocalDateTime dataTransacao;
    private LocalDateTime dataAgendamento;
    private double valorTransacao;
    private String tipoTransacao;
    private String localidade;
    private String dispositivo;
    private boolean ehFraude = false;

    @JsonCreator
    public TransacaoModel(
            @JsonProperty("idContaOrigem") UUID idContaOrigem,
            @JsonProperty("idContaDestino") UUID idContaDestino,
            @JsonProperty("dataTransacao") LocalDateTime dataTransacao,
            @JsonProperty("dataAgendamento") LocalDateTime dataAgendamento,
            @JsonProperty("valorTransacao") double valorTransacao,
            @JsonProperty("tipoTransacao") String tipoTransacao,
            @JsonProperty("localidade") String localidade,
            @JsonProperty("dispositivo") String dispositivo,
            @JsonProperty("ehFraude") boolean ehFraude) {
        this.id = UUID.randomUUID();
        this.idContaOrigem = idContaOrigem;
        this.idContaDestino = idContaDestino;
        this.dataTransacao = dataTransacao;
        this.dataAgendamento = dataAgendamento;
        this.valorTransacao = valorTransacao;
        this.tipoTransacao = tipoTransacao;
        this.localidade = localidade;
        this.dispositivo = dispositivo;
        this.ehFraude = ehFraude;
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
        return ehFraude;
    }

    public void setEhFralde(boolean ehFralde) {
        this.ehFraude = ehFralde;
    }
}
