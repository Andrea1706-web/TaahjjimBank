package util;

import model.*;
import service.UsuarioService;

import java.util.List;

public class MensagensErro {

    // Erros Gerais
    public static final String ERRO_DESERIALIZACAO = "Erro ao deserializar o body JSON.";
    public static final String ERRO_VALIDACAO = "Erro de validação nos campos obrigatórios.";

    // Transacao
    public static final String TIPO_TRANSACAO_NAO_SUPORTADA = "Tipo de transação não suportado.";
    public static final String TRANSACAO_INVALIDA = "Transação inválida.";
    public static final String TRANSACAO_NAO_ENCONTRADA = "Transação não encontrada.";

    // Agendamento
    public static final String DATA_AGENDAMENTO_INVALIDA = "Data de agendamento inválida.";
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
    public static final String USUARIO_NAO_ENCONTRADO = "Usuário não encontrado.";
    public static final String USUARIO_DUPLICADO = "Usuário já cadastrado com esse nome.";

    // Produto
    public static final String PRODUTO_DUPLICADO = "Produto com esse nome já cadastrado.";
    public static final String PRODUTO_NAO_ENCONTRADO = "Produto não encontrado.";

    // Segurança / Token
    public static final String TOKEN_INVALIDO = "Token inválido ou expirado.";
    public static final String TOKEN_NAO_FORNECIDO = "Token de autenticação não fornecido.";
}



