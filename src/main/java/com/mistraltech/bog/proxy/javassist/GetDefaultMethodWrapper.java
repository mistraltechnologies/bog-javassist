package com.mistraltech.bog.proxy.javassist;

import com.mistraltech.bog.core.annotation.GetsPropertyDefault;
import com.mistraltech.bog.proxy.javassist.util.JavassistClassUtils;
import javassist.CtClass;
import javassist.CtMethod;

import static com.mistraltech.bog.proxy.javassist.util.NameUtils.deCapitalise;
import static com.mistraltech.bog.proxy.javassist.util.NameUtils.removePrefix;

public class GetDefaultMethodWrapper extends MethodWrapper {
    private static final String GET_DEFAULT_METHOD_PREFIX = "getDefault";

    public GetDefaultMethodWrapper(CtMethod getDefaultMethod) {
        super(getDefaultMethod);
    }

    public static boolean hasGetDefaultMethodSignature(CtMethod ctMethod) {
        return hasGetDefaultMethodParameters(ctMethod)
                && hasGetDefaultMethodPropertyName(ctMethod);
    }

    private static boolean hasGetDefaultMethodPropertyName(CtMethod ctMethod) {
        return ctMethod.hasAnnotation(GetsPropertyDefault.class) || isConventionalGetDefaultMethodName(ctMethod.getName());
    }

    private static boolean isConventionalGetDefaultMethodName(String name) {
        return name.startsWith(GET_DEFAULT_METHOD_PREFIX);
    }

    private static boolean hasGetDefaultMethodParameters(CtMethod ctMethod) {
        final CtClass[] parameterTypes = JavassistClassUtils.getParameterTypes(ctMethod);
        return parameterTypes.length == 0;
    }

    public String getPropertyName() {
        final GetsPropertyDefault getsPropertyDefaultAnnotation = JavassistClassUtils.getAnnotation(wrappedMethod, GetsPropertyDefault.class);

        if (getsPropertyDefaultAnnotation != null) {
            return getsPropertyDefaultAnnotation.value();
        }

        if (!hasGetDefaultMethodPropertyName(wrappedMethod)) {
            throw new IllegalArgumentException(
                    String.format("Get-default method name '%s' was expected to start with prefix '%s'",
                            wrappedMethod.getName(), GET_DEFAULT_METHOD_PREFIX));
        }

        return deCapitalise(removePrefix(wrappedMethod.getName(), GET_DEFAULT_METHOD_PREFIX));
    }

    public String getName() {
        return wrappedMethod.getName();
    }

    public CtMethod getCtMethod() {
        return wrappedMethod;
    }
}
