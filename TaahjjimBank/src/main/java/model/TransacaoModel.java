package model;

import com.fasterxml.jackson.annotation.*;
import util.ContaExistente;
import util.ValidationUtil;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransacaoModel {
    private UUID id;

    @NotNull(message = "idContaOrigem é obrigatório")
    @ContaExistente
    private UUID idContaOrigem;

    @NotNull(message = "idContaDestino é obrigatório")
    @ContaExistente
    private UUID idContaDestino;

    @NotNull(message = "dataTransacao é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dataTransacao;

    @NotNull(message = "dataAgendamento é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dataAgendamento;

    @NotNull(message = "valorTransacao é obrigatório")
    @Positive(message = "O valor da transação deve ser maior que zero")
    private Double valorTransacao;

    @NotNull(message = "tipoTransacao é obrigatório")
    private eTipoTransacao tipoTransacao;

    @NotNull(message = "localidade é obrigatório")
    private String localidade;

    @NotNull(message = "dispositivo é obrigatório")
    private eDispositivo dispositivo;

    private boolean ehFraude = false;

    @JsonCreator
    public TransacaoModel(
            @JsonProperty("idContaOrigem") UUID idContaOrigem,
            @JsonProperty("idContaDestino") UUID idContaDestino,
            @JsonProperty("dataTransacao") LocalDateTime dataTransacao,
            @JsonProperty("dataAgendamento") LocalDateTime dataAgendamento,
            @JsonProperty("valorTransacao") double valorTransacao,
            @JsonProperty("tipoTransacao") eTipoTransacao tipoTransacao,
            @JsonProperty("localidade") String localidade,
            @JsonProperty("dispositivo") eDispositivo dispositivo) {
        this.id = UUID.randomUUID();
        this.idContaOrigem = idContaOrigem;
        this.idContaDestino = idContaDestino;
        this.dataTransacao = dataTransacao;
        this.dataAgendamento = dataAgendamento;
        this.valorTransacao = valorTransacao;
        this.tipoTransacao = tipoTransacao;
        this.localidade = localidade;
        this.dispositivo = dispositivo;
        ValidationUtil.validar(this);
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
}
