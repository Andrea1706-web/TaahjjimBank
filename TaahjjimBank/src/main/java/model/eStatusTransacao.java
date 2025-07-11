package model;

public enum eStatusTransacao {
    INICIADA,   // criada e em processamento
    AGENDADA,   // programada para processamento futuro (data posterior)
    CONLCUIDA, // concluida com sucesso
    REJEITADA,  // falhou ou foi recusada durante o processamento
    CANCELADA   // agendamento cancelado pelo usuário pagador
}
