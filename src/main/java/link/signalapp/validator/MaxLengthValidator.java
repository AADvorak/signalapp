package link.signalapp.validator;

import link.signalapp.ApplicationProperties;
import lombok.RequiredArgsConstructor;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class MaxLengthValidator implements ConstraintValidator<MaxLength, String> {

    private final ApplicationProperties applicationProperties;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s == null || s.length() <= applicationProperties.getMaxNameLength();
    }

}
