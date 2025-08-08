package service.interfaces;

import java.util.List;

public interface iListarService<T> extends iCrudService<T> {
    List<T> listar();
}