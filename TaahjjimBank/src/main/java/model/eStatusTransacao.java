package model;

public enum eStatusTransacao {
    INICIADA,   // criada e em processamento
    AGENDADA,   // programada para processamento futuro (data posterior)
    CONCLUIDA, // concluida com sucesso
    REJEITADA,  // falhou ou foi recusada durante o processamento
    EXPIRADA, // saldo insuficiente na conta do pagador
    CANCELADA   // agendamento cancelado pelo usuário pagador
}
