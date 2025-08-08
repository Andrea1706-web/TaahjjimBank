package model;

import jakarta.validation.constraints.NotBlank;
import model.TransacaoModel;
import util.MensagensErro;

public class TransacaoPix extends TransacaoModel {
    @NotBlank
    private String numeroContaDestino;

    @Override
    public void validarEspecifica(boolean isAgendada) {
        if (numeroContaDestino == null || numeroContaDestino.isBlank()) {
            throw new IllegalArgumentException(MensagensErro.CONTA_DESTINO_NAO_ENCONTRADA);
        }
    }
}
