package util;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ContaExistenteValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ContaExistente {
    String message() default "Conta bancária não existe";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
