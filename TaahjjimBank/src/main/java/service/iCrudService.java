package service;

public interface iCrudService<T> {
    T obter(String chave);

    T criar();

}
