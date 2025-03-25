CREATE TABLE IF NOT EXISTS TB_CARTOES (
    id INT PRIMARY KEY,
    numero_cartao VARCHAR(16),
    validade VARCHAR(4),
    codigo VARCHAR(3),
    numero_conta VARCHAR(10)
);


INSERT INTO TB_CARTOES (id, numero_cartao, validade, codigo, numero_conta)
VALUES (1, '123546877', '1024', '123', '1243569812');
