package service;

import model.ProdutoModel;
import java.util.List;

public interface ListarService<T> extends CrudService<T> {
    List<T> listar();
}