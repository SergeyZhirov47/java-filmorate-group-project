package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static java.util.Objects.isNull;

public class MinDateValidator implements ConstraintValidator<MinDate, LocalDate> {
    private LocalDate minDate = null;

    @Override
    public void initialize(MinDate constraintAnnotation) {
        try {
            this.minDate = LocalDate.parse(constraintAnnotation.minDate());
        } catch (DateTimeParseException exp) {
            minDate = null;
        }
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (isNull(minDate)) {
            return false;
        }

        return value.isAfter(minDate);
    }
}
