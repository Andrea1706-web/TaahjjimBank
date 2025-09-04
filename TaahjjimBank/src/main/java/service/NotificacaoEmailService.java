package service;

import com.amazonaws.services.simpleemail.*;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import dto.NotificacaoDTO;
import util.Consts;
import util.MensagensErro;

import java.time.LocalDateTime;

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
            throw new IllegalStateException(MensagensErro.ERRO_SES_FROM_NAO_CONFIGURADO);
        }
    }

       public void enviarResultadoLiquidacaoSeNaoEnviado(NotificacaoDTO dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()) return;

        String outcome = dto.isSucesso() ? "SUCESSO" : "ERRO";
        String lockKey = Consts.PATH_BUCKET_NOTIFICACOES + dto.getTransacaoId() + "-" + outcome + ".json";

        // idempotência: se já existe o lock, não reenvia
        if (s3.doesObjectExist(bucket, lockKey)) return;

        // monta e envia o e-mail
        String app = System.getenv().getOrDefault("APP_NAME", "ZupBank");
        String assunto = dto.isSucesso()
                ? "[" + app + "] PIX agendado liquidado com sucesso"
                : "[" + app + "] Falha na liquidação do PIX agendado";

        String bodyTxt = dto.isSucesso()
                ? String.format("Olá %s,%n%nSeu PIX agendado foi liquidado com sucesso.%nValor: R$ %.2f%nConta de origem: %s%nData: %s%n%nAtt,%n%s",
                dto.getNomeUsuario(), dto.getValor(), dto.getNumeroContaOrigem(), LocalDateTime.now(), app)
                : String.format("Olá %s,%n%nSeu PIX agendado NÃO foi liquidado.%nValor: R$ %.2f%nConta de origem: %s%nMotivo: %s%nData: %s%n%nAtt,%n%s",
                dto.getNomeUsuario(), dto.getValor(), dto.getNumeroContaOrigem(), dto.getMotivoErro(), LocalDateTime.now(), app);

        String bodyHtml = dto.isSucesso()
                ? "<p>Olá <b>" + dto.getNomeUsuario() + "</b>,</p>"
                + "<p>Seu PIX agendado foi <b>liquidado com sucesso</b>.</p>"
                + "<ul>"
                + "<li><b>Valor:</b> R$ " + String.format("%.2f", dto.getValor()) + "</li>"
                + "<li><b>Conta de origem:</b> " + dto.getNumeroContaOrigem() + "</li>"
                + "<li><b>Data:</b> " + LocalDateTime.now() + "</li>"
                + "</ul><p>Att,<br/>" + app + "</p>"

                : "<p>Olá <b>" + dto.getNomeUsuario() + "</b>,</p>"
                + "<p>Seu PIX agendado <b>não foi liquidado</b>.</p>"
                + "<ul>"
                + "<li><b>Valor:</b> R$ " + String.format("%.2f", dto.getValor()) + "</li>"
                + "<li><b>Conta de origem:</b> " + dto.getNumeroContaOrigem() + "</li>"
                + "<li><b>Motivo:</b> " + (dto.getMotivoErro() == null ? "-" : dto.getMotivoErro()) + "</li>"
                + "<li><b>Data:</b> " + LocalDateTime.now() + "</li>"
                + "</ul><p>Att,<br/>" + app + "</p>";

        Destination destination = new Destination().withToAddresses(dto.getEmail());
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
}
