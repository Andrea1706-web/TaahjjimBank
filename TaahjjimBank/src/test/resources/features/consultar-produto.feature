# language: pt
Funcionalidade: Consultar produtos bancários pelo nome cadastrado

  Cenário: Consultar produto com sucesso
    Dado que o produto "Fundo Alfa" existe no sistema
    Quando eu consultar o produto "Fundo Alfa"
    Então a resposta deve conter os dados do produto "Fundo Alfa"

  Cenário: Consultar produto que não existe
    Dado que o produto "Fundo Beta" não existe no sistema
    Quando eu consultar o produto "Fundo Beta"
    Então a resposta deve conter a mensagem "Registro não encontrado"