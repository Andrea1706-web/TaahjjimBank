# language: pt
Funcionalidade: Consultar produto existente

      Cenário: Consultar produto
            Dado que o produto "Fundo Alfa" está cadastrado
            Quando eu realizar uma requisição GET para "produto/Fundo Alfa"
            Então o serviço deve retornar o status 200
            E a resposta deve conter os dados do produto "Fundo Alfa"