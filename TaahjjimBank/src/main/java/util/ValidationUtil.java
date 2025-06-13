package util;

import jakarta.validation.*;

import java.util.Set;

public class ValidationUtil {  // Renomeei para ValidationUtil para evitar conflito com jakarta.validation.Validation
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    private ValidationUtil() {} // Utilitário, não instanciar

    public static <T> void validar(T objeto) {
        Set<ConstraintViolation<T>> violations = validator.validate(objeto);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> v : violations) {
                sb.append(v.getPropertyPath()).append(": ").append(v.getMessage()).append("\n");
            }
            throw new IllegalArgumentException(sb.toString());
        }
    }
}
