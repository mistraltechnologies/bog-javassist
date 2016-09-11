package com.mistraltech.bog.proxy.javassist;

import com.mistraltech.bog.core.Builder;
import com.mistraltech.bog.core.annotation.BuildsProperty;
import com.mistraltech.bog.core.annotation.ConstructorParameter;
import com.mistraltech.bog.proxy.javassist.util.JavassistClassUtils;
import javassist.CtClass;
import javassist.CtMethod;

import static com.mistraltech.bog.proxy.javassist.util.JavassistClassUtils.getAnnotation;
import static com.mistraltech.bog.proxy.javassist.util.NameUtils.deCapitalise;
import static com.mistraltech.bog.proxy.javassist.util.NameUtils.removePrefix;

public class BuilderMethodWrapper {
    private static final String BUILDER_METHOD_PREFIX = "with";

    private final CtMethod builderMethod;

    public BuilderMethodWrapper(CtMethod builderMethod) {
        this.builderMethod = builderMethod;
    }

    public String getBuiltPropertyName() {
        final BuildsProperty buildsPropertyAnnotation = getAnnotation(builderMethod, BuildsProperty.class);

        if (buildsPropertyAnnotation != null) {
            return buildsPropertyAnnotation.value();
        }

        if (!hasBuilderMethodPropertyName(builderMethod)) {
            throw new IllegalArgumentException(
                    String.format("Builder method name '%s' was expected to start with prefix '%s'",
                            builderMethod.getName(), BUILDER_METHOD_PREFIX));
        }

        return deCapitalise(removePrefix(builderMethod.getName(), BUILDER_METHOD_PREFIX));
    }

    public static boolean hasBuilderMethodSignature(CtMethod ctMethod, CtClass builderCtClass) {
        return hasBuilderMethodParameters(ctMethod)
                && hasBuilderMethodPropertyName(ctMethod)
                && hasBuilderMethodReturnType(ctMethod, builderCtClass);
    }

    private static boolean hasBuilderMethodPropertyName(CtMethod ctMethod) {
        return ctMethod.hasAnnotation(BuildsProperty.class) || isConventionalBuilderMethodName(ctMethod.getName());
    }

    private static boolean isConventionalBuilderMethodName(String name) {
        return name.startsWith(BUILDER_METHOD_PREFIX);
    }

    private static boolean hasBuilderMethodParameters(CtMethod ctMethod) {
        final CtClass[] parameterTypes = JavassistClassUtils.getParameterTypes(ctMethod);
        return parameterTypes.length == 1;
    }

    private static boolean hasBuilderMethodReturnType(CtMethod ctMethod, CtClass builderCtClass) {
        final CtClass returnType = JavassistClassUtils.getReturnType(ctMethod);
        final CtClass builderInterfaceCtClass = JavassistClassUtils.getCtClass(Builder.class.getName());

        return JavassistClassUtils.isTypeInBounds(returnType, builderCtClass, builderInterfaceCtClass);
    }

    public CtClass getParameterType() {
        return JavassistClassUtils.getSingleParameterType(builderMethod);
    }

    public String getName() {
        return builderMethod.getName();
    }

    public boolean takesBuilder() {
        return isBuilder(getParameterType());
    }

    private static boolean isBuilder(CtClass parameterType) {
        final CtClass builderInterfaceCtClass = JavassistClassUtils.getCtClass(Builder.class.getName());
        return JavassistClassUtils.isSubTypeOf(parameterType, builderInterfaceCtClass);
    }

    public CtMethod getCtMethod() {
        return builderMethod;
    }

    public int getConstructorParameterIndex() {
        final ConstructorParameter constructorPropertyAnnotation = getAnnotation(builderMethod, ConstructorParameter.class);
        return (constructorPropertyAnnotation != null) ? constructorPropertyAnnotation.value() : -1;
    }
}
