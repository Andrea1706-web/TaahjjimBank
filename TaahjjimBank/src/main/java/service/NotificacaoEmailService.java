package service;

import com.amazonaws.services.simpleemail.*;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import model.TransacaoModel;
import model.UsuarioModel;
import util.Consts;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificacaoEmailService {

    private final AmazonSimpleEmailService ses;
    private final AmazonS3 s3;
    private final String bucket;
    private final String from;
    private final String replyTo;

    public NotificacaoEmailService() {
        this.ses = AmazonSimpleEmailServiceClientBuilder.defaultClient();
        this.s3 = AmazonS3ClientBuilder.defaultClient();
        this.bucket = System.getenv("BUCKET_NAME");
        this.from = System.getenv("SES_FROM");
        this.replyTo = System.getenv("SES_REPLY_TO");
        if (from == null || from.isBlank()) {
            throw new IllegalStateException("SES_FROM não configurado.");
        }
    }

    public void enviarResultadoLiquidacaoSeNaoEnviado(UsuarioModel usuario,
                                                      TransacaoModel tx,
                                                      boolean sucesso,
                                                      String motivoErroOuVazio) {
        if (usuario == null || usuario.getEmail() == null || usuario.getEmail().isBlank()) return;

        String outcome = sucesso ? "SUCESSO" : "ERRO";
        String lockKey = gerarKeyIdempotencia(tx.getId(), outcome);

        // idempotência: se já existe o lock, não reenvia
        if (s3.doesObjectExist(bucket, lockKey)) return;

        // monta e envia o e-mail
        String to = usuario.getEmail();
        String app = System.getenv().getOrDefault("APP_NAME", "ZupBank");
        String assunto = sucesso
                ? "[" + app + "] PIX agendado liquidado com sucesso"
                : "[" + app + "] Falha na liquidação do PIX agendado";

        String bodyTxt = sucesso
                ? String.format("Olá %s,%n%nSeu PIX agendado foi liquidado com sucesso.%nValor: R$ %.2f%nConta de origem: %s%nData: %s%n%nAtt,%n%s",
                usuario.getNomeCompleto(), tx.getValorTransacao(), tx.getNumeroContaOrigem(), LocalDateTime.now(), app)
                : String.format("Olá %s,%n%nSeu PIX agendado NÃO foi liquidado.%nValor: R$ %.2f%nConta de origem: %s%nMotivo: %s%nData: %s%n%nAtt,%n%s",
                usuario.getNomeCompleto(), tx.getValorTransacao(), tx.getNumeroContaOrigem(), motivoErroOuVazio, LocalDateTime.now(), app);

        String bodyHtml = sucesso
                ? "<p>Olá <b>" + usuario.getNomeCompleto() + "</b>,</p>"
                + "<p>Seu PIX agendado foi <b>liquidado com sucesso</b>.</p>"
                + "<ul>"
                + "<li><b>Valor:</b> R$ " + String.format("%.2f", tx.getValorTransacao()) + "</li>"
                + "<li><b>Conta de origem:</b> " + tx.getNumeroContaOrigem() + "</li>"
                + "<li><b>Data:</b> " + LocalDateTime.now() + "</li>"
                + "</ul><p>Att,<br/>" + app + "</p>"
                : "<p>Olá <b>" + usuario.getNomeCompleto() + "</b>,</p>"
                + "<p>Seu PIX agendado <b>não foi liquidado</b>.</p>"
                + "<ul>"
                + "<li><b>Valor:</b> R$ " + String.format("%.2f", tx.getValorTransacao()) + "</li>"
                + "<li><b>Conta de origem:</b> " + tx.getNumeroContaOrigem() + "</li>"
                + "<li><b>Motivo:</b> " + (motivoErroOuVazio == null ? "-" : motivoErroOuVazio) + "</li>"
                + "<li><b>Data:</b> " + LocalDateTime.now() + "</li>"
                + "</ul><p>Att,<br/>" + app + "</p>";

        Destination destination = new Destination().withToAddresses(to);
        Content subject = new Content().withCharset("UTF-8").withData(assunto);
        Body body = new Body()
                .withText(new Content().withCharset("UTF-8").withData(bodyTxt))
                .withHtml(new Content().withCharset("UTF-8").withData(bodyHtml));
        Message message = new Message().withSubject(subject).withBody(body);
        SendEmailRequest request = new SendEmailRequest()
                .withSource(from)
                .withDestination(destination)
                .withMessage(message);

        if (replyTo != null && !replyTo.isBlank()) {
            request = request.withReplyToAddresses(replyTo);
        }

        ses.sendEmail(request);

        // grava o lock de idempotência
        s3.putObject(bucket, lockKey,
                "{\"sentAt\":\"" + LocalDateTime.now() + "\",\"outcome\":\"" + outcome + "\"}");
    }

    private String gerarKeyIdempotencia(UUID transacaoId, String outcome) {
        return Consts.PATH_BUCKET_NOTIFICACOES + transacaoId + "-" + outcome + ".json";
    }
}
