package com.example.signalapp.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ModuleValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Module {
    String message() default "must be not empty and not contain spaces";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
