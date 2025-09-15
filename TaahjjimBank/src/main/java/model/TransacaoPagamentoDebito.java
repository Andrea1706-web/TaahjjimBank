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

    public TransacaoPagamentoDebito() {
        this.setTipoTransacao(eTipoTransacao.PAGAMENTO_DEBITO);
    }

    public String getReferenciaEstabelecimento() {
        return referenciaEstabelecimento;
    }

    public void setReferenciaEstabelecimento(String referenciaEstabelecimento) {
        this.referenciaEstabelecimento = referenciaEstabelecimento;
    }

    public String getModalidadePagamento() {
        return modalidadePagamento;
    }

    public void setModalidadePagamento(String modalidadePagamento) {
        this.modalidadePagamento = modalidadePagamento;
    }

    @Override
    public void validarEspecifica(boolean isAgendada) {
        if (referenciaEstabelecimento == null || referenciaEstabelecimento.isBlank())
            throw new IllegalArgumentException("Campo 'referenciaEstabelecimento' é obrigatório.");
        if (isAgendada)
            throw new IllegalArgumentException("Pagamentos com débito NÃO podem ser agendados.");
    }
}
