package com.mistraltech.bog.examples.simple.builders;

import com.mistraltech.bog.core.Builder;
import com.mistraltech.bog.core.annotation.Builds;
import com.mistraltech.bog.core.annotation.ConstructorParameter;
import com.mistraltech.bog.examples.model.generics.Box;

import static com.mistraltech.bog.proxy.javassist.JavassistBuilderGenerator.builderOf;

@Builds(Box.class)
public interface BadBuilder2 extends Builder<Box<String>> {
    static BadBuilder2 aBox() {
        return builderOf(BadBuilder2.class);
    }

    @ConstructorParameter(0)
    BadBuilder2 withContent1(String content);

    // Negative constructor parameter is not allowed...

    @ConstructorParameter(-1)
    BadBuilder2 withContent2(String content);
}
