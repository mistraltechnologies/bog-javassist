package com.mistraltech.bog.examples.simple.builders;

import com.mistraltech.bog.core.Builder;
import com.mistraltech.bog.core.annotation.Builds;
import com.mistraltech.bog.core.annotation.ConstructorParameter;
import com.mistraltech.bog.examples.model.PostCode;

import static com.mistraltech.bog.proxy.javassist.JavassistBuilderGenerator.builderOf;

@Builds(PostCode.class)
public interface PostCodeBuilder extends Builder<PostCode> {

    static PostCodeBuilder aPostCode() {
        return builderOf(PostCodeBuilder.class);
    }

    @ConstructorParameter(0)
    PostCodeBuilder withOuter(String outer);

    @ConstructorParameter(1)
    PostCodeBuilder withInner(String inner);
}
