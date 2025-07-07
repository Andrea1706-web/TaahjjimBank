# language: pt
Funcionalidade: Cadastrar um produto bancário

  Cenário: Cadastro bem sucedido de produto bancario
    Dado que eu tenho os dados do produto:
      | nome       | descricao           | taxaAdministracao | grauRisco | categoria  | tipoProduto        |
      | Fundo Alfa | Fundo de renda fixa | 0.15              | BAIXO     | RENDA_FIXA | FUNDO_INVESTIMENTO |
    Quando eu solicitar o cadastro do produto
    Então o serviço de cadastro deve retornar o status 201 e o produto deve ser criado com sucesso

  Cenário: Não permitir cadastro de produto com nome duplicado
    Dado que um produto está cadastrado no sistema com os seguintes dados:
      | nome       | descricao           | taxaAdministracao | grauRisco | categoria  | tipoProduto        |
      | Fundo Alfa | Fundo de renda fixa | 0.15              | BAIXO     | RENDA_FIXA | FUNDO_INVESTIMENTO |
    Quando eu solicitar o cadastro de um produto com o mesmo nome "Fundo Alfa"
    Então o serviço deve retornar um erro de duplicidade informando "Nome já existente"