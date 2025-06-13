package util;

import model.ContaBancariaModel;
import jakarta.validation.*;


import java.util.List;
import java.util.UUID;

public class ContaExistenteValidator implements ConstraintValidator<ContaExistente, UUID> {

    private ContaBancariaModel contaBancariaModel;

    public boolean isValid(UUID value, ConstraintValidatorContext context) {
        if (value == null) return false;
        List<ContaBancariaModel> contas = (List<ContaBancariaModel>) contaBancariaModel;
        return contaBancariaModel.existeContaComId(value, contas);
    }
}