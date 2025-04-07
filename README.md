# TaahjjimBank
História de Usuário:

Visão Geral do Produto
O TaahjjimBank será uma solução bancária digital para oferecer serviços básicos inicialmente, permitindo que o time de desenvolvimento treine habilidades de Java, Orientação a Objetos, e, futuramente, Spring Framework. As funcionalidades iniciais visam fornecer uma base sólida para expandir o sistema conforme o time avança em complexidade.

Histórias de Usuário

1. Cadastro e Validação de Usuário
Como cliente do TaahjjimBank,  
quero criar uma conta vinculada ao meu CPF, nome, email e senha,  
para acessar os serviços bancários e gerenciar minhas finanças.

Critérios de Aceitação:
- Deve ser possível cadastrar um usuário com dados únicos.
- O sistema deve validar que o CPF não está duplicado no banco.
- O email deve seguir um formato válido.
- A senha será armazenada de forma simples (sem hash ou salt neste momento).

2. Login
Como cliente do TaahjjimBank,  
quero acessar minha conta usando email e senha,  
para garantir que apenas eu tenha acesso aos meus dados bancários.

Critérios de Aceitação:
- O sistema deve validar o email e a senha fornecidos.
- Apenas usuários com credenciais corretas podem acessar o sistema.
- Mensagens claras devem ser exibidas em caso de erro de login.

3. Cadastro de Contas
Como cliente,  
quero criar uma conta bancária associada ao meu cadastro,  
para realizar operações financeiras como depósitos, transferências e saques.

Critérios de Aceitação:
- O sistema deve permitir a criação de Conta Corrente ou Conta Poupança.
- Cada conta deve ter um número único.
- O saldo inicial deve ser zero.

4. Depósito em Conta
Como cliente,  
quero depositar dinheiro na minha conta,  
para aumentar meu saldo disponível.

Critérios de Aceitação:
- O sistema deve validar que o valor do depósito é positivo.
- Após o depósito, o saldo da conta deve refletir o novo valor.

5. Saque em Conta
Como cliente,  
quero realizar saques da minha conta,  
para utilizar meu saldo em dinheiro.

Critérios de Aceitação:
- O sistema deve validar que o valor sacado não ultrapassa o saldo disponível.
- Após o saque, o saldo deve ser atualizado corretamente.
- Saques não podem ser realizados em contas com saldo insuficiente.

6. Transferência entre Contas
Como cliente,  
quero transferir dinheiro da minha conta para outra conta do TaahjjimBank,  
para facilitar pagamentos e transações com outros usuários.

Critérios de Aceitação:
- Deve ser possível transferir valores entre contas do mesmo banco.
- O sistema deve validar que o saldo é suficiente antes de realizar a transferência.
- Após a transferência, os saldos de ambas as contas devem ser atualizados.

7. Consulta de Saldo
Como cliente,  
quero consultar o saldo da minha conta,  
para acompanhar minhas finanças.

Critérios de Aceitação:
- O saldo atual deve ser exibido ao cliente de forma clara.
- O sistema deve buscar o saldo atualizado em tempo real.

Features Planejadas Futuramente

Histórico de Movimentações
- Registro detalhado de todas as transações realizadas em cada conta.

Sistema de PIX
Como cliente,  
quero realizar transferências instantâneas usando PIX,  
para enviar e receber dinheiro com mais facilidade.

Critérios de Aceitação:
- O sistema deve permitir o registro de chaves PIX (CPF, email, telefone ou chave aleatória).
- Transferências PIX devem ser instantâneas e seguras.
- O sistema deve registrar as transferências no histórico de movimentações.

API RESTful
- Expor as funcionalidades do sistema para integração com aplicativos externos.

Classes Básicas do Sistema
1. Usuário: Representa os clientes do banco, com informações pessoais e credenciais (email e senha).
2. Conta (Abstrata): Classe base para diferentes tipos de conta (Corrente e Poupança).
3. ContaCorrente e ContaPoupanca: Especializações com comportamentos específicos.
4. TaahjjimBank: Gerenciador central para todas as operações do banco.

Objetivos do Time
- Treinar conceitos de Orientação a Objetos, como herança, polimorfismo, e encapsulamento.
- Praticar boas práticas de desenvolvimento, como validações, segurança básica, e testes unitários.
- Gradualmente introduzir Spring Boot para transformar o sistema em uma API escal
- 
