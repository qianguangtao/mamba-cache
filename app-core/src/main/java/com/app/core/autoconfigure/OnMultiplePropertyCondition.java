package com.app.core.autoconfigure;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Objects;

public class OnMultiplePropertyCondition implements Condition {
    @Override
    public boolean matches(@NotNull ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 获取注解上配置的信息
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ConditionalOnMultipleProperty.class.getName());
        String prefix = (String) annotationAttributes.get("prefix");
        String name = (String) annotationAttributes.get("name");
        String propertyName = Objects.isNull(prefix) ? name : prefix + "." + name;
        String[] values = (String[]) annotationAttributes.get("havingValue");
        if (0 == values.length) {
            return false;
        }

        // 获取环境中的配置的信息（这里也就是application.properties的信息）
        String propertyValue = context.getEnvironment().getProperty(propertyName);
        // 有一个匹配上就ok
        if (propertyValue != null) {
            for (String havingValue : values) {
                if (havingValue.equalsIgnoreCase(propertyValue)) {
                    return true;
                }
            }
        }
        return false;
    }
}
