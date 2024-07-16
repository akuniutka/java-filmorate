package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsAfterValidator.class)
@Documented
public @interface IsAfter {

    String message() default "must be after {value}";

    String value();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
