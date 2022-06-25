package com.example.signalapp.validator;

import com.example.signalapp.ApplicationProperties;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class MinLengthValidator implements ConstraintValidator<MinLength, String> {

    private final ApplicationProperties applicationProperties;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s == null || s.length() >= applicationProperties.getMinPasswordLength();
    }
}
