# language: pt
Funcionalidade: Listar todos os produtos

  Cenário: Listar todos os produtos
    Dado que existem produtos cadastrados
    Quando realizar uma requisição GET para "produto"
    Então o serviço deve retornar o status 200
    E a lista de produtos deve conter pelo menos 1 item