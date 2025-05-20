package service;

public interface CrudService<T> {
    T obter(String chave);

    T criar();

}
