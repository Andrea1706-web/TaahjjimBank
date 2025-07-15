package model;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransacaoModel {
    private UUID id;

    @NotNull(message = "numeroContaOrigem é obrigatório")
    private String numeroContaOrigem;

    @NotNull(message = "numeroContaDestino é obrigatório")
    private String numeroContaDestino;

    private LocalDateTime dataTransacao;

    @NotNull(message = "dataAgendamento é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dataAgendamento;

    @NotNull(message = "valorTransacao é obrigatório")
    @Positive(message = "O valor da transação deve ser maior que zero")
    private Double valorTransacao;

    @NotNull(message = "tipoTransacao é obrigatório")
    private eTipoTransacao tipoTransacao;

    @NotNull
    @NotBlank(message = "localidade é obrigatório")
    private String localidade;

    @NotNull(message = "dispositivo é obrigatório")
    private eDispositivo dispositivo;

    private boolean ehFraude = false;

    private eStatusTransacao statusTransacao;

    @JsonCreator
    public TransacaoModel(
            @JsonProperty("numeroContaOrigem") String numeroContaOrigem,
            @JsonProperty("numeroContaDestino") String numeroContaDestino,
            @JsonProperty("dataAgendamento") LocalDateTime dataAgendamento,
            @JsonProperty("valorTransacao") double valorTransacao,
            @JsonProperty("tipoTransacao") eTipoTransacao tipoTransacao,
            @JsonProperty("localidade") String localidade,
            @JsonProperty("dispositivo") eDispositivo dispositivo){
        this.id = UUID.randomUUID();
        this.numeroContaOrigem = numeroContaOrigem;
        this.numeroContaDestino = numeroContaDestino;
        this.dataTransacao = LocalDateTime.now();
        this.dataAgendamento = dataAgendamento;
        this.valorTransacao = valorTransacao;
        this.tipoTransacao = tipoTransacao;
        this.localidade = localidade;
        this.dispositivo = dispositivo;
        this.statusTransacao = null;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNumeroContaOrigem() {
        return numeroContaOrigem;
    }

    public void setNumeroContaOrigem(String numeroContaOrigem) {
        this.numeroContaOrigem = numeroContaOrigem;
    }

    public String getNumeroContaDestino() {
        return numeroContaDestino;
    }

    public void setNumeroContaDestino(String numeroContaDestino) {
        this.numeroContaDestino = numeroContaDestino;
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

    public eTipoTransacao getTipoTransacao() {
        return tipoTransacao;
    }

    public void setTipoTransacao(eTipoTransacao tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public eDispositivo getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(eDispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }

    public boolean isEhFraude() {
        return ehFraude;
    }

    public void setEhFraude(boolean ehFraude) {
        this.ehFraude = ehFraude;
    }

    public eStatusTransacao getStatusTransacao() {
        return statusTransacao;
    }
    public void setStatusTransacao(eStatusTransacao statusTransacao) {
        this.statusTransacao = statusTransacao;
    }

}
