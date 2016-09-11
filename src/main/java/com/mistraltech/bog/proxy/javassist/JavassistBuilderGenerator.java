package com.mistraltech.bog.proxy.javassist;

import com.mistraltech.bog.core.AbstractBuilder;
import com.mistraltech.bog.core.propertybuilder.ValueContainer;
import com.mistraltech.bog.proxy.javassist.util.JavaReflectionUtils;
import com.mistraltech.bog.proxy.javassist.util.JavassistClassUtils;
import com.mistraltech.bog.proxy.javassist.util.PropertyDescriptorLocator;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtPrimitiveType;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generates a BOG {@link com.mistraltech.bog.core.AbstractBuilder} implementation for a given builder interface.
 * <p>
 * The supplied builder interface is required to have a {@link com.mistraltech.bog.core.annotation.Builds} annotation.
 * <p>
 * The interface should declare methods beginning "with" for each relevant
 * property on the target class e.g. withName(..). These methods should take a single parameter
 * of the type of the property. There is no requirement to supply a "with" method for all properties.
 */
public class JavassistBuilderGenerator {

    /**
     * Generate a builder class instance for the specified interface.
     *
     * @param builderInterface an interface for a builder
     * @param <TB>             the type of builder to be generated
     * @return the new builder instance
     */
    public static <TB> TB builderOf(Class<TB> builderInterface) {
        Class<TB> builderClass = getBuilderClass(builderInterface);
        return JavaReflectionUtils.createInstance(builderClass);
    }

    /**
     * Get the builder implementation class for the given builder interface.
     *
     * @param builderInterface the builder interface
     * @param <TB>             the type of the builder interface
     * @return a class that implements the builder
     */
    @SuppressWarnings("unchecked")
    private static <TB> Class<TB> getBuilderClass(Class<TB> builderInterface) {
        final String builderClassName = generateBuilderClassName(builderInterface);

        try {
            return (Class<TB>) Class.forName(builderClassName);
        } catch (ClassNotFoundException e) {
            return generateBuilderClass(builderInterface, builderClassName);
        }
    }

    private static <TB> String generateBuilderClassName(Class<TB> builderInterface) {
        return builderInterface.getName() + "BogImpl";
    }

    private static <TB> Class<TB> generateBuilderClass(Class<TB> builderInterface, String builderClassName) {
        final BuilderInterfaceWrapper<TB> builderInterfaceWrapper = new BuilderInterfaceWrapper<>(builderInterface);
        return generateBuilderClass(builderInterfaceWrapper, builderClassName);
    }

    private static <TB> Class<TB> generateBuilderClass(BuilderInterfaceWrapper<TB> builderInterface, String builderClassName) {
        final CtClass builderCtSuperClass = JavassistClassUtils.getCtClass(AbstractBuilder.class.getName());
        final CtClass builderCtClass = buildBuilderCtClass(builderClassName, builderInterface, builderCtSuperClass);

        final Class<TB> builderClass = JavassistClassUtils.getClassFrom(builderCtClass);
        builderCtClass.detach();

        return builderClass;
    }

    private static <TB> CtClass buildBuilderCtClass(String builderClassName, BuilderInterfaceWrapper<TB> builderInterface,
            CtClass builderCtSuperClass) {
        final CtClass builderCtClass = ClassPool.getDefault().makeClass(builderClassName, builderCtSuperClass);
        builderCtClass.addInterface(builderInterface.getCtInterface());

        generateDefaultConstructor(builderCtClass);

        generateFromMethods(builderInterface, builderCtClass);

        for (BuilderMethodWrapper builderMethodDecl : builderInterface.getBuilderMethods()) {
            generateBuilderMethod(builderCtClass, builderInterface, builderMethodDecl);
        }

        for (GetterMethodWrapper getterMethodDecl : builderInterface.getGetterMethods()) {
            generateGetterMethod(builderCtClass, getterMethodDecl);
        }

        generateConstructMethod(builderInterface, builderCtClass);

        generateUpdateMethod(builderInterface, builderCtClass);

        generatePostUpdateMethod(builderInterface, builderCtClass);

        return builderCtClass;
    }

    private static <TB> void generateConstructMethod(BuilderInterfaceWrapper<TB> builderInterface, CtClass builderCtClass) {
        final CtClass builderCtSuperClass = JavassistClassUtils.getCtClass(AbstractBuilder.class.getName());
        StringBuilder paramsBuilder = new StringBuilder();
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("{\n");

        List<BuilderMethodWrapper> constructorParams = builderInterface.getConstructorParams();
        for (BuilderMethodWrapper constructorParam : constructorParams) {
            if (paramsBuilder.length() > 0) {
                paramsBuilder.append(',');
            }

            if (constructorParam != null) {
                String fieldName = getFieldName(constructorParam.getBuiltPropertyName());
                String typeName = constructorParam.getParameterType().getName();

                if (constructorParam.getParameterType().isPrimitive()) {
                    CtPrimitiveType parameterType = (CtPrimitiveType) constructorParam.getParameterType();
                    paramsBuilder.append(String.format("((%s) %s.get()).%sValue()", parameterType.getWrapperName(), fieldName, typeName));
                } else {
                    paramsBuilder.append(String.format("(%s) %s.get()", typeName, fieldName));
                }
            } else {
                paramsBuilder.append("null");
            }
        }

        String params = paramsBuilder.toString();
        bodyBuilder.append(String.format("return new %s(%s);\n", builderInterface.getBuiltClass().getName(), params));
        bodyBuilder.append("}\n");
        String body = bodyBuilder.toString();

        CtMethod constructMethod = JavassistClassUtils.getMethod(builderCtSuperClass, "construct");
        JavassistClassUtils.addMethod(builderCtClass, Modifier.PROTECTED, constructMethod, body);
    }

    private static <TB> void generateUpdateMethod(BuilderInterfaceWrapper<TB> builderInterface, CtClass builderCtClass) {
        final CtClass builderCtSuperClass = JavassistClassUtils.getCtClass(AbstractBuilder.class.getName());
        final CtClass builtCtClass = builderInterface.getBuiltClass();
        final Class<?> builtClass = JavassistClassUtils.getLoadedClass(builtCtClass);
        final PropertyDescriptorLocator propertyDescriptorHelper = new PropertyDescriptorLocator(builtClass);

        final StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("{\n");

        for (BuilderMethodWrapper builderMethod : builderInterface.getBuilderMethods()) {

            String builtPropertyName = builderMethod.getBuiltPropertyName();
            String fieldName = getFieldName(builtPropertyName);

            PropertyDescriptor builtPropertyDescriptor = propertyDescriptorHelper.findPropertyDescriptor(builtPropertyName);
            if (builtPropertyDescriptor != null) {
                Method builtPropertyWriteMethod = builtPropertyDescriptor.getWriteMethod();

                if (builtPropertyWriteMethod != null && builtPropertyWriteMethod.getParameterCount() == 1) {
                    bodyBuilder.append(String.format("((%s)$1).%s((%s) %s.get());\n",
                            builtCtClass.getName(), builtPropertyWriteMethod.getName(),
                            builtPropertyWriteMethod.getParameterTypes()[0].getName(), fieldName));
                }
            }
        }

        bodyBuilder.append("}\n");
        String body = bodyBuilder.toString();

        CtMethod assignMethod = JavassistClassUtils.getMethod(builderCtSuperClass, "assign");
        JavassistClassUtils.addMethod(builderCtClass, Modifier.PROTECTED, assignMethod, body);
    }

    private static <TB> void generatePostUpdateMethod(BuilderInterfaceWrapper<TB> builderInterface, CtClass builderCtClass) {
        final CtClass builderCtSuperClass = JavassistClassUtils.getCtClass(AbstractBuilder.class.getName());

        final StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("{\n");

        for (BuilderMethodWrapper builderMethod : builderInterface.getBuilderMethods()) {
            String builtPropertyName = builderMethod.getBuiltPropertyName();
            String fieldName = getFieldName(builtPropertyName);

            bodyBuilder.append(fieldName).append(".reset();\n");
        }

        bodyBuilder.append("}\n");
        String body = bodyBuilder.toString();

        CtMethod postUpdateMethod = JavassistClassUtils.getMethod(builderCtSuperClass, "postUpdate");
        JavassistClassUtils.addMethod(builderCtClass, Modifier.PROTECTED, postUpdateMethod, body);
    }

    private static void generateDefaultConstructor(CtClass builderCtClass) {
        JavassistClassUtils.addConstructor(builderCtClass);
    }

    private static <TB> void generateFromMethods(BuilderInterfaceWrapper<TB> builderInterface, CtClass builderCtClass) {
        List<CtMethod> fromMethods = builderInterface.getFromMethods();

        for (CtMethod fromMethod : fromMethods) {
            StringBuffer bodyBuilder = new StringBuffer();
            bodyBuilder.append("{\n");

            final CtClass fromMethodParameterCtClass = JavassistClassUtils.getSingleParameterType(fromMethod);
            final Class<?> fromMethodParameterClass = JavassistClassUtils.getLoadedClass(fromMethodParameterCtClass);

            PropertyDescriptorLocator propertyDescriptorHelper = new PropertyDescriptorLocator(fromMethodParameterClass);

            Set<String> builtProperties = new HashSet<String>();
            for (BuilderMethodWrapper builderMethod : builderInterface.getBuilderMethods()) {
                String propertyName = builderMethod.getBuiltPropertyName();

                if (!builtProperties.contains(propertyName)) {
                    final PropertyDescriptor propertyDescriptor = propertyDescriptorHelper.findPropertyDescriptor(propertyName);

                    if (propertyDescriptor != null) {
                        final Method propertyReadMethod = propertyDescriptor.getReadMethod();
                        final String parameter = "$1." + propertyReadMethod.getName() + "()";

                        final CtClass propertyCtClass = JavassistClassUtils.getCtClass(propertyReadMethod.getReturnType().getName());
                        final CtClass builderMethodParameterCtClass = builderMethod.getParameterType();

                        if (builderMethodParameterCtClass.equals(propertyCtClass)) {
                            // This builder method takes the built property type directly
                            // We have a winner...
                            builtProperties.add(propertyName);
                            bodyBuilder.append(builderMethod.getName()).append("(").append(parameter).append(");\n");
                        }
                    }
                }
            }

            bodyBuilder.append("return this;\n");
            bodyBuilder.append("}\n");
            String body = bodyBuilder.toString();

            JavassistClassUtils.addMethod(builderCtClass, Modifier.PUBLIC, fromMethod, body);
        }
    }

    private static <TB> void generateBuilderMethod(CtClass builderCtClass, BuilderInterfaceWrapper<TB> builderInterface, BuilderMethodWrapper builderMethodDecl) {
        final String propertyName = builderMethodDecl.getBuiltPropertyName();

        final String propertyBuilderFieldName = getFieldName(propertyName);
        final String propertyBuilderTypeName = ValueContainer.class.getName();
        final CtClass propertyBuilderCtClass = JavassistClassUtils.getCtClass(propertyBuilderTypeName);

        if (!JavassistClassUtils.hasField(builderCtClass, propertyBuilderFieldName)) {
            final String fieldInitializer = getFieldInitialiser(builderInterface, builderMethodDecl, propertyBuilderTypeName);
            JavassistClassUtils.addField(builderCtClass, propertyBuilderCtClass, propertyBuilderFieldName, fieldInitializer);
        }

        final String methodBody = generateBuilderMethodBody(propertyBuilderFieldName, builderMethodDecl);

        JavassistClassUtils.addMethod(builderCtClass, Modifier.PUBLIC, builderMethodDecl.getCtMethod(), methodBody);
    }

    private static <TB> String getFieldInitialiser(BuilderInterfaceWrapper<TB> builderInterface, BuilderMethodWrapper builderMethodDecl, String propertyBuilderTypeName) {
        // If there is a default method for the field of the form getDefault<Field> that returns a ValuePicker, make a call to it
        GetDefaultMethodWrapper getDefaultMethod = builderInterface.getGetDefaultMethod(builderMethodDecl.getBuiltPropertyName());

        if (getDefaultMethod != null) {
            return String.format("%s.valueContainer(%s())", propertyBuilderTypeName, getDefaultMethod.getName());
        } else {
            final String parameterTypeName = builderMethodDecl.getParameterType().getName();
            return String.format("%s.valueContainer(%s.class)", propertyBuilderTypeName, parameterTypeName);
        }
    }

    private static void generateGetterMethod(CtClass builderCtClass, GetterMethodWrapper getterMethodDecl) {
        final String propertyName = getterMethodDecl.getPropertyName();
        final String propertyBuilderFieldName = getFieldName(propertyName);

        if (!JavassistClassUtils.hasField(builderCtClass, propertyBuilderFieldName)) {
            throw new RuntimeException("Illegal getter method " + getterMethodDecl.getName());
        }

        final String methodBody = generateGetterMethodBody(getterMethodDecl, propertyBuilderFieldName);

        JavassistClassUtils.addMethod(builderCtClass, Modifier.PUBLIC, getterMethodDecl.getCtMethod(), methodBody);
    }

    private static String getFieldName(String propertyName) {
        return propertyName;
    }

    private static String generateBuilderMethodBody(String propertyBuilderFieldName, BuilderMethodWrapper builderMethod) {
        String parameter = autobox(builderMethod, "$1");
        return String.format("{ this.%s.set(%s); return this; }", propertyBuilderFieldName, parameter);
    }

    private static String generateGetterMethodBody(GetterMethodWrapper getterMethodDecl, String propertyBuilderFieldName) {
        return String.format("{ return (%s) %s.get(); }", getterMethodDecl.getReturnType().getName(), propertyBuilderFieldName);
    }

    private static String autobox(BuilderMethodWrapper builderMethod, String paramName) {
        if (builderMethod.getParameterType().isPrimitive()) {
            CtPrimitiveType primitiveType = (CtPrimitiveType) builderMethod.getParameterType();
            return String.format("new %s(%s)", primitiveType.getWrapperName(), paramName);
        } else {
            return paramName;
        }
    }
}
