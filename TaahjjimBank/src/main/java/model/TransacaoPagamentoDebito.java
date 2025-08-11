package model;

import jakarta.validation.constraints.NotBlank;
import model.enums.eDispositivo;
import model.enums.eTipoTransacao;

import java.time.LocalDateTime;

public class TransacaoPagamentoDebito extends TransacaoModel {
    @NotBlank
    private String referenciaEstabelecimento;
    @NotBlank
    private String modalidadePagamento;

    @Override
    public void validarEspecifica(boolean isAgendada) {
        if (referenciaEstabelecimento == null || referenciaEstabelecimento.isBlank())
            throw new IllegalArgumentException("Campo 'referenciaEstabelecimento' é obrigatório.");
        if (isAgendada)
            throw new IllegalArgumentException("Pagamentos com débito NÃO podem ser agendados.");
        // Valide modalidade (ex: com enum)
    }
}
