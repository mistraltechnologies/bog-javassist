package com.mistraltech.bog.examples.simple.builders;

import com.mistraltech.bog.core.Builder;
import com.mistraltech.bog.core.annotation.Builds;
import com.mistraltech.bog.core.annotation.ConstructorParameter;
import com.mistraltech.bog.examples.model.Phone;

import static com.mistraltech.bog.proxy.javassist.JavassistBuilderGenerator.builderOf;

@Builds(Phone.class)
public interface PhoneBuilder extends Builder<Phone> {
    static PhoneBuilder aPhone() {
        return builderOf(PhoneBuilder.class);
    }

    @ConstructorParameter(0)
    PhoneBuilder withCode(String code);

    @ConstructorParameter(1)
    PhoneBuilder withNumber(String number);
}
