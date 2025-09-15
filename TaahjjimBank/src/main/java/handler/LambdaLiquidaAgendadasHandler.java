package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dto.NotificacaoDTO;
import model.TransacaoModel;
import model.TransacaoPagamentoDebito;
import model.TransacaoPix;
import model.ContaBancariaModel;
import model.UsuarioModel;
import service.command.PixCommand;
import service.ContaBancariaService;
import service.UsuarioService;
import service.DriverS3;
import util.MensagensErro;

import java.util.List;

public class LambdaLiquidaAgendadasHandler implements RequestHandler<SQSEvent, Void> {

    private final ObjectMapper objectMapper;
    private final AmazonSQS sqs;

    public LambdaLiquidaAgendadasHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.registerSubtypes(TransacaoPix.class, TransacaoPagamentoDebito.class);

        this.sqs = AmazonSQSClientBuilder.defaultClient();
    }

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        String bucketName = System.getenv("BUCKET_NAME");
        String filaNotificacao = System.getenv("FILA_NOTIFICACAO");

        DriverS3<TransacaoModel> driverS3 = new DriverS3<>(bucketName, TransacaoModel.class);
        ContaBancariaService contaService = new ContaBancariaService(bucketName, null);
        UsuarioService usuarioService = new UsuarioService(bucketName, null);

        PixCommand pixCommand = new PixCommand();

        for (SQSEvent.SQSMessage message : event.getRecords()) {
            TransacaoModel transacao = null;
            try {
                // desserializa a transação recebida da fila de liquidação
                transacao = objectMapper
                        .readerFor(TransacaoModel.class)
                        .readValue(message.getBody());

                // executa liquidação imediata (CRÍTICO)
                List<TransacaoModel> resultado = pixCommand.executar(transacao, false, driverS3, contaService);

                // dados do usuário pagador
                ContaBancariaModel contaOrigem = contaService.obter(transacao.getNumeroContaOrigem());
                UsuarioModel pagador = usuarioService.obterPorDocumento(contaOrigem.getCpf());

                // tenta notificar (NÃO CRÍTICO)
                try {
                    NotificacaoDTO dto = new NotificacaoDTO();
                    dto.setTransacaoId(transacao.getId());
                    dto.setEmail(pagador.getEmail());
                    dto.setNomeUsuario(pagador.getNomeCompleto());
                    dto.setValor(transacao.getValorTransacao());
                    dto.setNumeroContaOrigem(transacao.getNumeroContaOrigem());
                    dto.setSucesso(true);
                    dto.setMotivoErro(null);

                    enviarNotificacao(filaNotificacao, dto);

                } catch (Exception notifErr) {
                    context.getLogger().log(MensagensErro.ERRO_NOTIFICAR_TRANSACAO_LIQUIDADA
                            + notifErr.getMessage()); // não relança e liquidação segue válida
                }

                context.getLogger().log("Transação liquidada: " + resultado);

            } catch (Exception liquidaErr) {
                context.getLogger().log(MensagensErro.ERRO_PROCESSAMENTO_SQS + " Detalhe: " + liquidaErr.getMessage());
                try {
                    if (transacao != null) {
                        ContaBancariaModel contaOrigem = contaService.obter(transacao.getNumeroContaOrigem());
                        UsuarioModel pagador = usuarioService.obterPorDocumento(contaOrigem.getCpf());

                        NotificacaoDTO dto = new NotificacaoDTO();
                        dto.setTransacaoId(transacao.getId());
                        dto.setEmail(pagador.getEmail());
                        dto.setNomeUsuario(pagador.getNomeCompleto());
                        dto.setValor(transacao.getValorTransacao());
                        dto.setNumeroContaOrigem(transacao.getNumeroContaOrigem());
                        dto.setSucesso(false);
                        dto.setMotivoErro(liquidaErr.getMessage());

                        enviarNotificacao(filaNotificacao, dto);
                    }
                } catch (Exception nested) {
                    context.getLogger().log(MensagensErro.ERRO_PREPARAR_NOTIFICACAO + " Detalhe: " + nested.getMessage());
                }

                // relança → mensagem volta para fila/DLQ
                throw new RuntimeException(liquidaErr);
            }
        }
        return null;
    }

    private void enviarNotificacao(String filaNotificacao, NotificacaoDTO dto) {
        try {
            String payload = objectMapper.writeValueAsString(dto);
            sqs.sendMessage(new SendMessageRequest(filaNotificacao, payload));
        } catch (Exception ex) {
            throw new RuntimeException(MensagensErro.ERRO_ENVIAR_NOTIFICACAO, ex);
        }
    }
}
