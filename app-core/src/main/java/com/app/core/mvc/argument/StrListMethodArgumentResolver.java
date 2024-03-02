package com.app.core.mvc.argument;

import cn.hutool.core.collection.ListUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/17 16:39
 * @description: 前端url参数"a,b,c"使用List接收
 */
@Slf4j
public class StrListMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {
    @Override
    protected NamedValueInfo createNamedValueInfo(final MethodParameter parameter) {
        final StrListPathVariable ann = parameter.getParameterAnnotation(StrListPathVariable.class);
        return ann != null ? new NamedValueInfo(ann.name(), ann.required(), null) : new NamedValueInfo("", false, null);
    }

    @Override
    protected Object resolveName(final String name, final MethodParameter parameter, final NativeWebRequest request)
        throws Exception {
        final Map<String, String> uriTemplateVars = (Map<String, String>)request
            .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        return ListUtil.of(uriTemplateVars.get(name).split(","));
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(StrListPathVariable.class);
    }
}
