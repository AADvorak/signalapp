package link.signalapp.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ModuleValidator implements ConstraintValidator<Module, String> {

    @Override
    public boolean isValid(String module, ConstraintValidatorContext constraintValidatorContext) {
        return module != null && !module.isEmpty() && !module.contains(" ");
    }

}
