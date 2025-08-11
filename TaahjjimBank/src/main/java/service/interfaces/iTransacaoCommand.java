package service.interfaces;

import model.TransacaoModel;
import model.enums.eTipoTransacao;
import service.ContaBancariaService;
import service.DriverS3;

import java.util.List;

public interface iTransacaoCommand {
    boolean aceita(eTipoTransacao tipo);

    List<TransacaoModel> executar(TransacaoModel transacao, //objeto contendo os dados especificos da transação a ser processada (TransacaoPix, TransacaoPagamentoDebito...)
                                  boolean isAgendada,
                                  DriverS3<TransacaoModel> driverS3,
                                  ContaBancariaService contaService);
}
