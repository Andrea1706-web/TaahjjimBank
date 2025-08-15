package model;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;
import model.enums.*;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipoTransacao")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TransacaoPix.class, name = "PIX"),
        @JsonSubTypes.Type(value = TransacaoPagamentoDebito.class, name = "PAGAMENTO_DEBITO")
})
public abstract class TransacaoModel {

    private UUID id;

    @NotNull(message = "numeroContaOrigem é obrigatório")
    private String numeroContaOrigem;

    private LocalDateTime dataTransacao;

    @NotNull(message = "dataAgendamento é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dataAgendamento;

    @NotNull(message = "valorTransacao é obrigatório")
    @Positive(message = "O valor da transação deve ser maior que zero")
    private Double valorTransacao;

    @NotNull(message = "tipoTransacao é obrigatório")
    private eTipoTransacao tipoTransacao;

    @NotBlank(message = "localidade é obrigatória")
    private String localidade;

    @NotNull(message = "dispositivo é obrigatório")
    private eDispositivo dispositivo;

    private boolean ehFraude = false;
    private eStatusTransacao statusTransacao;

    // Construtor base (usado pelas subclasses)
    public TransacaoModel(String numeroContaOrigem,
                          LocalDateTime dataAgendamento,
                          Double valorTransacao,
                          eTipoTransacao tipoTransacao,
                          String localidade,
                          eDispositivo dispositivo) {
        this.id = UUID.randomUUID();
        this.numeroContaOrigem = numeroContaOrigem;
        this.dataTransacao = LocalDateTime.now();
        this.dataAgendamento = dataAgendamento;
        this.valorTransacao = valorTransacao;
        this.tipoTransacao = tipoTransacao;
        this.localidade = localidade;
        this.dispositivo = dispositivo;
    }

    public TransacaoModel() {
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

    public Double getValorTransacao() {
        return valorTransacao;
    }

    public void setValorTransacao(Double valorTransacao) {
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

    public abstract void validarEspecifica(boolean isAgendada);


}
