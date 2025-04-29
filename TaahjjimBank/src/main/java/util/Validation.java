package util;
import javax.validation.*;
import java.util.Set;

public class Validation {
    private static final ValidatorFactory factory = javax.validation.Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    private Validation() {} // Utilitário, não instanciar

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
