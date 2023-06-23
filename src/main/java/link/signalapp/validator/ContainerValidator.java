package link.signalapp.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class ContainerValidator implements ConstraintValidator<Container, String> {

    public static final String[] validValues = {"main", "left", "right", "modal"};

    @Override
    public boolean isValid(String container, ConstraintValidatorContext constraintValidatorContext) {
        return container == null || Arrays.asList(validValues).contains(container);
    }

}
