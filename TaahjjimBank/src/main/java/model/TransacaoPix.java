package model;

import jakarta.validation.constraints.NotBlank;
import model.TransacaoModel;
import model.enums.eTipoTransacao;
import util.MensagensErro;

public class TransacaoPix extends TransacaoModel {

    @NotBlank
    private String numeroContaDestino;

    public TransacaoPix() {
        this.setTipoTransacao(eTipoTransacao.PIX);
    }

    public String getNumeroContaDestino() {
        return numeroContaDestino;
    }

    public void setNumeroContaDestino(String numeroContaDestino) {
        this.numeroContaDestino = numeroContaDestino;
    }

    @Override
    public void validarEspecifica(boolean isAgendada) {
        if (numeroContaDestino == null || numeroContaDestino.isBlank()) {
            throw new IllegalArgumentException(MensagensErro.CONTA_DESTINO_NAO_ENCONTRADA);
        }
    }
}
