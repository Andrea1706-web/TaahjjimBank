package service;

public interface CrudService<T> {
    T obter(String chave);
    void criar(T objeto);
}
