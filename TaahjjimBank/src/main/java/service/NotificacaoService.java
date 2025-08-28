package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import model.ContaBancariaModel;
import model.TransacaoModel;
import model.UsuarioModel;
import model.TransacaoPix;

import java.util.List;
import java.util.Map;

public class NotificacaoService {

    private final ContaBancariaService contaService;
    private final UsuarioService usuarioService;
    private final AmazonSNS snsClient;
    private final String topicArn;

    public NotificacaoService(String bucketName, String topicArn) {
        this.contaService = new ContaBancariaService(bucketName, null);
        this.usuarioService = new UsuarioService(bucketName, null);
        this.snsClient = AmazonSNSClientBuilder.defaultClient();
        this.topicArn = topicArn;
    }

    public void notificarTransacao(TransacaoModel transacao) {
        String emailPagador = obterEmailDoPagador(transacao.getNumeroContaOrigem());

        String mensagem = String.format(
                "Olá! A sua transação agendada no valor de %.2f foi enviada para a conta destino: %s com sucesso.",
                transacao.getValorTransacao(),
                (transacao instanceof TransacaoPix) ? ((TransacaoPix) transacao).getNumeroContaDestino() : "N/A"
        );

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> payload = Map.of(
                    "email", emailPagador,
                    "mensagem", mensagem
            );

            snsClient.publish(topicArn, mapper.writeValueAsString(payload));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar notificação para SNS", e);
        }
    }

    private String obterEmailDoPagador(String numeroContaOrigem) {
        ContaBancariaModel conta = contaService.obter(numeroContaOrigem);
        if (conta == null) {
            throw new IllegalArgumentException("Conta de origem não encontrada: " + numeroContaOrigem);
        }

        String cpfPagador = conta.getCpf();

        List<UsuarioModel> usuarios = usuarioService.listar();
        return usuarios.stream()
                .filter(u -> u.getDocumento().equalsIgnoreCase(cpfPagador))
                .findFirst()
                .map(UsuarioModel::getEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado para CPF: " + cpfPagador));
    }
}
