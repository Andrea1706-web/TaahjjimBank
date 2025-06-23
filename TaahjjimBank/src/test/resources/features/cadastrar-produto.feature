# language: pt
Funcionalidade: Cadastrar um produto bancário

  Cenário: Cadastro de produto
    Dado que eu tenho os dados do produto:
      | nome       | descricao            | taxaAdministracao | grauRisco | categoria   | tipoProduto        |
      | Fundo Alfa | Fundo de renda fixa  | 0.15              | BAIXO     | RENDA_FIXA  | FUNDO_INVESTIMENTO |
    Quando eu realizar uma requisição POST para "produto/"
    Então o serviço deve retornar o status 201