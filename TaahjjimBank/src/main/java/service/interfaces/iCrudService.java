package service.interfaces;

public interface iCrudService<T> {
    T obter(String chave);

    default T criar(T objeto) {
        throw new UnsupportedOperationException("Método criar(T objeto) não implementado nesta classe");
    }

    /* mantido para compatibilidade, deve ser removido apos refatoração de todas as classes */
    @Deprecated
    default T criar() {
        throw new UnsupportedOperationException("Método  criar() não implementado nesta classe.");
    }
}
