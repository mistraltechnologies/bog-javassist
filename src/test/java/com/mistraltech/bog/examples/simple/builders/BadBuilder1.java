package com.mistraltech.bog.examples.simple.builders;

import com.mistraltech.bog.core.Builder;
import com.mistraltech.bog.core.annotation.Builds;
import com.mistraltech.bog.core.annotation.ConstructorParameter;
import com.mistraltech.bog.examples.model.generics.Box;

import static com.mistraltech.bog.proxy.javassist.JavassistBuilderGenerator.builderOf;

@Builds(Box.class)
public interface BadBuilder1 extends Builder<Box<String>> {
    static BadBuilder1 aBox() {
        return builderOf(BadBuilder1.class);
    }

    @ConstructorParameter(0)
    BadBuilder1 withContent1(String content);

    // Duplicate constructor parameter is not allowed...

    @ConstructorParameter(0)
    BadBuilder1 withContent2(String content);
}
