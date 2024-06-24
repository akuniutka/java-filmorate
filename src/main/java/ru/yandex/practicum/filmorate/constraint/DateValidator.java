package ru.yandex.practicum.filmorate.constraint;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateValidator implements ConstraintValidator<IsAfter, LocalDate> {

    private LocalDate date;

    @Override
    public void initialize(IsAfter constraintAnnotation) {
        date = LocalDate.parse(constraintAnnotation.value(), DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate == null || localDate.isAfter(date);
    }
}
