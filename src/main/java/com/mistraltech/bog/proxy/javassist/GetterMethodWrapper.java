package com.mistraltech.bog.proxy.javassist;

import com.mistraltech.bog.core.annotation.GetsProperty;
import com.mistraltech.bog.proxy.javassist.util.JavassistClassUtils;
import javassist.CtClass;
import javassist.CtMethod;

import static com.mistraltech.bog.proxy.javassist.util.NameUtils.deCapitalise;
import static com.mistraltech.bog.proxy.javassist.util.NameUtils.removePrefix;

public class GetterMethodWrapper {
    private static final String GETTER_METHOD_PREFIX = "get";

    private final CtMethod getterMethod;

    public GetterMethodWrapper(CtMethod getterMethod) {
        this.getterMethod = getterMethod;
    }

    public static boolean hasGetterMethodSignature(CtMethod ctMethod) {
        return hasGetterMethodParameters(ctMethod)
                && hasGetterMethodPropertyName(ctMethod);
    }

    private static boolean hasGetterMethodPropertyName(CtMethod ctMethod) {
        return ctMethod.hasAnnotation(GetsProperty.class) || isConventionalGetterMethodName(ctMethod.getName());
    }

    private static boolean isConventionalGetterMethodName(String name) {
        return name.startsWith(GETTER_METHOD_PREFIX);
    }

    private static boolean hasGetterMethodParameters(CtMethod ctMethod) {
        final CtClass[] parameterTypes = JavassistClassUtils.getParameterTypes(ctMethod);
        return parameterTypes.length == 0;
    }

    public String getPropertyName() {
        final GetsProperty getsPropertyAnnotation = JavassistClassUtils.getAnnotation(getterMethod, GetsProperty.class);

        if (getsPropertyAnnotation != null) {
            return getsPropertyAnnotation.value();
        }

        if (!hasGetterMethodPropertyName(getterMethod)) {
            throw new IllegalArgumentException(
                    String.format("Getter method name '%s' was expected to start with prefix '%s'",
                            getterMethod.getName(), GETTER_METHOD_PREFIX));
        }

        return deCapitalise(removePrefix(getterMethod.getName(), GETTER_METHOD_PREFIX));
    }

    public String getName() {
        return getterMethod.getName();
    }

    public CtMethod getCtMethod() {
        return getterMethod;
    }

    public CtClass getReturnType() {
        return JavassistClassUtils.getReturnType(getterMethod);
    }
}
