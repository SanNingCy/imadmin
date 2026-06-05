package com.web4x.common.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ImShiroDisabledCondition implements Condition
{
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata)
    {
        return ImShiroConditionSupport.isImShiroExplicitlyDisabled(context.getEnvironment());
    }
}
