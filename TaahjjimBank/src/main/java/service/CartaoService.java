package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.CartaoModel;
import org.springframework.stereotype.Service;

/*
Implementa a interface CrudService<CartaoModel>, definindo os métodos obter(String numeroCartao) e criar(CartaoModel cartao).
A persistência no S3 é realizada diretamente nesses métodos, onde:
- "obter": busca o cartão no S3 usando o número do cartão como chave.
- "criar": salva um novo cartão no S3, utilizando o número do cartão como chave para armazená-lo.
*/

@Service
public class CartaoService implements CrudService<CartaoModel> {

    private final DriverS3<CartaoModel> driverS3;
    private final ObjectMapper objectMapper;

    public CartaoService(String bucketName) {
        this.driverS3 = new DriverS3<>(bucketName, CartaoModel.class);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public CartaoModel obter(String numeroCartao) {
        String key = "dados/" + numeroCartao + ".json";
        return driverS3.read(key).orElse(null);
    }

    @Override
    public void criar(CartaoModel cartao) {
        String key = "dados/" + cartao.getNumeroCartao() + ".json";
        driverS3.save(key, cartao);
    }
}
