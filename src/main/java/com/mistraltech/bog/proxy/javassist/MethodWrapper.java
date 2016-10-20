package com.mistraltech.bog.proxy.javassist;

import javassist.CtMethod;
import javassist.NotFoundException;

import java.util.Arrays;

public class MethodWrapper {
    protected final CtMethod wrappedMethod;

    public MethodWrapper(CtMethod wrappedMethod) {
        this.wrappedMethod = wrappedMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodWrapper that = (MethodWrapper) o;

        try {
            return wrappedMethod.getName().equals(that.wrappedMethod.getName()) &&
                    Arrays.asList(wrappedMethod.getParameterTypes()).equals(Arrays.asList(that.wrappedMethod.getParameterTypes()));
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int hashCode() {
        return wrappedMethod.getName().hashCode();
    }

    @Override
    public String toString() {
        return wrappedMethod.getName() + wrappedMethod.getSignature();
    }
}
