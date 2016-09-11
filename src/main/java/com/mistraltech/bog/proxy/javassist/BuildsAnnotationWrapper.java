package com.mistraltech.bog.proxy.javassist;

import com.mistraltech.bog.core.annotation.Builds;

/**
 * Wrapper for accessing properties of the BOG @Builds annotation.
 */
class BuildsAnnotationWrapper {

    private final Builds annotation;

    /**
     * Constructor.
     *
     * @param owningClass the class having the @Builds annotation
     */
    public BuildsAnnotationWrapper(Class<?> owningClass) {
        this.annotation = getVerifiedBuildsAnnotation(owningClass);
    }

    /**
     * Get the {@link Builds} annotation on the supplied class. Performs basic validation checks, ensuring that
     * the annotation is present and has a non-null value.
     *
     * @param clazz the class
     * @return the annotation
     */
    private Builds getVerifiedBuildsAnnotation(Class<?> clazz) {
        final Builds buildsAnnotation = clazz.getAnnotation(Builds.class);

        if (buildsAnnotation == null) {
            throw new IllegalArgumentException("Missing @Builds annotation: " + clazz.getName());
        }

        if (buildsAnnotation.value() == null) {
            throw new IllegalArgumentException("@Builds annotation has null value: " + clazz.getName());
        }

        return buildsAnnotation;
    }

    /**
     * Gets the built class.
     *
     * @return the class that the annotation declares is being built
     */
    public Class<?> getBuiltClass() {
        return annotation.value();
    }
}
