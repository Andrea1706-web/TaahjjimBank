package util;

public class MensagensErro {

    // Geral
    public static final String ERRO_DESERIALIZACAO = "Erro ao deserializar o body JSON.";

    // Transacao
    public static final String TIPO_TRANSACAO_NAO_SUPORTADA = "Tipo de transação não suportado.";

    // Agendamento
    public static final String AGENDAMENTO_NAO_PERMITIDO = "Essa transação não permite agendamento.";

    // Cartão
    public static final String CARTAO_ID_DUPLICADO = "ID do cartão já existente.";
    public static final String CARTAO_NUMERO_DUPLICADO = "Número do cartão já existente.";

    // Conta bancária
    public static final String CONTA_ORIGEM_NAO_ENCONTRADA = "Conta origem não encontrada.";
    public static final String CONTA_DESTINO_NAO_ENCONTRADA = "Conta destino não encontrada.";
    public static final String CONTA_DUPLICADA = "Conta bancária já cadastrada.";
    public static final String SALDO_INSUFICIENTE = "Saldo insuficiente para realizar a transação.";

    // Usuário / Login
    public static final String USUARIO_OU_SENHA_INVALIDOS = "Usuário ou senha inválidos.";
    public static final String USUARIO_DUPLICADO = "Usuário já cadastrado com esse nome.";

    // Produto
    public static final String PRODUTO_DUPLICADO = "Produto com esse nome já cadastrado.";

    //Notificacao
    public static final String ERRO_PROCESSAMENTO_SQS = "Erro ao processar mensagem SQS.";
    public static final String ERRO_PREPARAR_NOTIFICACAO = "Erro ao preparar notificação de erro.";
    public static final String ERRO_ENVIAR_NOTIFICACAO = "Falha ao enviar mensagem para fila de notificação.";
    public static final String ERRO_SES_FROM_NAO_CONFIGURADO = "Configuração SES_FROM ausente.";
    public static final String ERRO_PROCESSAR_NOTIFICACAO = "Erro ao processar notificação.";
    public static final String ERRO_NOTIFICAR_TRANSACAO_LIQUIDADA = "Falha ao enviar notificação, mas liquidação foi concluída.";
}



