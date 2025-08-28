package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Map;
import java.util.Properties;

public class LambdaNotificacaoHandler implements RequestHandler<SNSEvent, Void> {

    @Override
    public Void handleRequest(SNSEvent event, Context context) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            for (SNSEvent.SNSRecord record : event.getRecords()) {
                Map<String, String> payload = mapper.readValue(record.getSNS().getMessage(), Map.class);
                String destinatario = payload.get("email");
                String mensagem = payload.get("mensagem");

                enviarEmail(destinatario, mensagem);
                context.getLogger().log("Email enviado para " + destinatario);
            }
        } catch (Exception e) {
            context.getLogger().log("Erro ao enviar email: " + e.getMessage());
        }
        return null;
    }

    private void enviarEmail(String destinatario, String corpo) throws Exception {
        String remetente = System.getenv("SMTP_USER");
        String senha = System.getenv("SMTP_PASS");
        String host = System.getenv("SMTP_HOST");
        String porta = System.getenv("SMTP_PORT");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", porta);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remetente, senha);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(remetente));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        message.setSubject("Notificação de Transação");
        message.setText(corpo);

        Transport.send(message);
    }
}
