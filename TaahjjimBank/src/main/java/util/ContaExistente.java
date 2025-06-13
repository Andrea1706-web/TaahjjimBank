package util;

import java.lang.annotation.*;
import jakarta.validation.*;

@Documented
@Constraint(validatedBy = ContaExistenteValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ContaExistente {
    String message() default "Conta bancária não existe";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
