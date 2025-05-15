package service;

public interface CrudService<T> {
    T obter(String chave);

    T criar(T model);


    //CartaoModel criarCartao(CartaoModel cartaoModel);
}
