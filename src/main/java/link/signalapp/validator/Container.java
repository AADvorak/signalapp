package link.signalapp.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ContainerValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Container {
    String message() default "must be null or have one of allowed values";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
