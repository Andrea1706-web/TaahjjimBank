package service.interfaces;

public interface iCrudService<T> {
    T obter(String chave);

    T criar();

}
