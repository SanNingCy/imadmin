package com.seekweb4.chat.agora.bean.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Constraint(validatedBy = EnumValidator.class)
public @interface EnumValid {
    Class<?> value() default Class.class;

    String message() default "Parameter value not in correct enumeration";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean ignoreEmpty() default true;
}