# language: pt
Funcionalidade: Listar todos os produtos

  Cenário: Listar todos os produtos
    Dado que existem produtos cadastrados
    Quando eu solicitar a listagem de produtos
    Então a lista de produtos deve conter pelo menos 1 item

  Cenário: Listar produtos quando não há produtos cadastrados
    Dado que não existem produtos cadastrados
    Quando eu solicitar a listagem de produtos
    Então a lista de produtos deve estar vazia
