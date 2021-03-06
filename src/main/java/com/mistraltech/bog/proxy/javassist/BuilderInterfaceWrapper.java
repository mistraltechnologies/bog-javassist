package com.mistraltech.bog.proxy.javassist;

import com.mistraltech.bog.core.Builder;
import com.mistraltech.bog.proxy.javassist.util.JavassistClassUtils;
import javassist.CtClass;
import javassist.CtMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Wraps and augments the builder interface.
 */
public class BuilderInterfaceWrapper<TB> {
    private final BuildsAnnotationWrapper buildsAnnotation;
    private final CtClass builderCtInterface;

    public BuilderInterfaceWrapper(Class<TB> builderInterface) {
        this.buildsAnnotation = new BuildsAnnotationWrapper(builderInterface);
        this.builderCtInterface = JavassistClassUtils.getCtClass(builderInterface.getName());
    }

    public CtClass getCtInterface() {
        return builderCtInterface;
    }

    public Set<BuilderMethodWrapper> getBuilderMethods() {
        Set<BuilderMethodWrapper> methods = new HashSet<>();

        for (CtMethod method : JavassistClassUtils.getMethods(builderCtInterface)) {
            if (BuilderMethodWrapper.hasBuilderMethodSignature(method, builderCtInterface)) {
                methods.add(new BuilderMethodWrapper(method));
            }
        }

        return methods;
    }

    public Set<GetterMethodWrapper> getGetterMethods() {
        Set<GetterMethodWrapper> methods = new HashSet<>();

        for (CtMethod method : JavassistClassUtils.getMethods(builderCtInterface)) {
            if (GetterMethodWrapper.hasGetterMethodSignature(method) && !GetDefaultMethodWrapper.hasGetDefaultMethodSignature(method)) {
                methods.add(new GetterMethodWrapper(method));
            }
        }

        return methods;
    }

    public CtClass getBuiltClass() {
        final Class<?> builtClass = buildsAnnotation.getBuiltClass();
        return JavassistClassUtils.getCtClass(builtClass.getName());
    }

    public List<CtMethod> getFromMethods() {
        List<CtMethod> methods = new ArrayList<CtMethod>();

        CtClass builtCtClass = getBuiltClass();

        for (CtMethod method : JavassistClassUtils.getMethods(builderCtInterface)) {
            if (hasFromMethodSignature(method, builtCtClass, builderCtInterface)) {
                methods.add(method);
            }
        }

        return methods;
    }

    private static boolean hasFromMethodSignature(CtMethod ctMethod, CtClass builtCtClass, CtClass builderCtClass) {
        final CtClass returnType = JavassistClassUtils.getReturnType(ctMethod);
        final CtClass[] parameterTypes = JavassistClassUtils.getParameterTypes(ctMethod);
        final CtClass builderInterfaceCtClass = JavassistClassUtils.getCtClass(Builder.class.getName());

        return parameterTypes.length == 1 &&
                ctMethod.getName().equals("from") &&
                JavassistClassUtils.isTypeInBounds(returnType, builderCtClass, builderInterfaceCtClass) &&
                JavassistClassUtils.isSubTypeOf(builtCtClass, parameterTypes[0]);
    }

    public List<BuilderMethodWrapper> getConstructorParams() {
        Set<BuilderMethodWrapper> builderMethods = getBuilderMethods();
        List<BuilderMethodWrapper> constructorParams = new ArrayList<>();

        for (BuilderMethodWrapper builderMethod : builderMethods) {
            Integer idx = builderMethod.getConstructorParameterIndex();
            if (idx != null) {
                if (idx < 0) {
                    throw new RuntimeException("Constructor parameter '" + builderMethod.getBuiltPropertyName() + "' has negative index " + idx);
                }

                growList(constructorParams, idx + 1);

                if (constructorParams.get(idx) != null) {
                    throw new RuntimeException("Constructor parameter '" + builderMethod.getBuiltPropertyName() + "' has duplicate index " + idx);
                }

                constructorParams.set(idx, builderMethod);
            }
        }

        return constructorParams;
    }

    private void growList(List<?> list, int toSize) {
        while(list.size() < toSize) {
            list.add(null);
        }
    }

    public GetDefaultMethodWrapper getGetDefaultMethod(String propertyName) {
        for (CtMethod method : JavassistClassUtils.getMethods(builderCtInterface)) {
            if (GetDefaultMethodWrapper.hasGetDefaultMethodSignature(method)) {
                GetDefaultMethodWrapper methodWrapper = new GetDefaultMethodWrapper(method);
                if (methodWrapper.getPropertyName().equals(propertyName)) {
                    return methodWrapper;
                }
            }
        }

        return null;
    }
}
