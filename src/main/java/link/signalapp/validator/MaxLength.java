package link.signalapp.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MaxLengthValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxLength {
    String message() default "Max length violation";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
