package com.mistraltech.bog.examples.simple.builders;

import com.mistraltech.bog.core.Builder;
import com.mistraltech.bog.core.annotation.Builds;
import com.mistraltech.bog.examples.model.Address;
import com.mistraltech.bog.examples.model.PostCode;

import static com.mistraltech.bog.proxy.javassist.JavassistBuilderGenerator.builderOf;

@Builds(Address.class)
public interface AddressBuilder extends Builder<Address> {

    static AddressBuilder anAddress() {
        return builderOf(AddressBuilder.class);
    }

    AddressBuilder withNumber(Integer number);

    AddressBuilder withPostCode(PostCode postCode);

    AddressBuilder withPostCode(Builder<? extends PostCode> postCode);
}
