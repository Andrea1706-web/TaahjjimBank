package service.command;

import model.TransacaoModel;
import model.enums.eStatusTransacao;
import model.enums.eTipoTransacao;
import service.interfaces.iTransacaoCommand;
import util.Consts;
import service.ContaBancariaService;
import service.DriverS3;
import util.MensagensErro;
import util.QuickCommandUtil;

import java.util.ArrayList;
import java.util.List;

public class DebitoCommand implements iTransacaoCommand {

    @Override
    public boolean aceita(eTipoTransacao tipo) {
        return tipo == eTipoTransacao.PAGAMENTO_DEBITO;
    }

    @Override
    public List<TransacaoModel> executar(TransacaoModel transacao, boolean isAgendada,
                                         DriverS3<TransacaoModel> driverS3,
                                         ContaBancariaService contaService) {

        transacao.validarEspecifica(isAgendada);

        if (isAgendada) {
            throw new IllegalArgumentException(MensagensErro.AGENDAMENTO_NAO_PERMITIDO);
        }

        var contaOrigem = contaService.obter(transacao.getNumeroContaOrigem());
        if (contaOrigem == null) {
            throw new IllegalArgumentException(MensagensErro.CONTA_ORIGEM_NAO_ENCONTRADA);
        }

        if (contaOrigem.getSaldo() < transacao.getValorTransacao()) {
            throw new IllegalArgumentException(MensagensErro.SALDO_INSUFICIENTE);
        }

        // liquidação imediata, não existe "contaDestino", salva somente origem
        // Atualiza saldo
        contaOrigem.setSaldo(contaOrigem.getSaldo() - transacao.getValorTransacao());
        contaService.salvar(contaOrigem);

        // Atualiza status
        transacao.setStatusTransacao(eStatusTransacao.CONCLUIDA);
        transacao.setDataTransacao(java.time.LocalDateTime.now());

        // Salva no extrato da origem
        String keyOrigem = Consts.PATH_BUCKET_TRANSACAO + transacao.getNumeroContaOrigem() + ".json";

        List<TransacaoModel> transacoes = driverS3.readList(keyOrigem, TransacaoModel.class).orElse(new ArrayList<>());
        transacoes.add(transacao);
        driverS3.saveList(keyOrigem, transacoes);

        return List.of(transacao);
    }

    public void rodarQCTransacao() {
        String clientId = "fb197df9-4fe7-4ae9-8ae8-3052868dfb14";
        String clientSecret = "xnxs3Yp6Q1O3EX838cpH9LwhMV8W82uYg27m9P3yddiRjQdft68V8XT5JJarnf2e";
        QuickCommandUtil qcUtil = new QuickCommandUtil(
                clientId,
                clientSecret,
                Consts.QUICK_COMMAD_ANALISA_TRANSACAO_POST_URL,
                Consts.QUICK_COMMAND_GET_URL_TEMPLATE,
                null
        );
        String payload = "{ \"input_data\": \"valor de entrada\" }";
        String resultado = qcUtil.runQuickCommand(payload, 5, "completed");
        System.out.println("Resultado do QC de Transação: " + resultado);

    }
}
