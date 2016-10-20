package com.mistraltech.bog.examples.defaults.builders;

import com.mistraltech.bog.core.Builder;
import com.mistraltech.bog.core.BuilderProperty;
import com.mistraltech.bog.core.annotation.Builds;
import com.mistraltech.bog.examples.model.Address;
import com.mistraltech.bog.examples.model.PostCode;

import java.util.function.Supplier;

import static com.mistraltech.bog.core.picker.ArrayRandomValuePicker.arrayRandomValuePicker;
import static com.mistraltech.bog.examples.defaults.builders.PostCodeBuilder.aPostCode;
import static com.mistraltech.bog.proxy.javassist.JavassistBuilderGenerator.builderOf;

@Builds(Address.class)
public interface AddressBuilder extends Builder<Address> {

    static AddressBuilder anAddress() {
        return builderOf(AddressBuilder.class);
    }

    AddressBuilder withNumber(Integer number);

    AddressBuilder withPostCode(PostCode postCode);

    AddressBuilder withPostCode(Builder<? extends PostCode> postCode);

    BuilderProperty<Integer> getNumber();

    default Supplier<Integer> getDefaultNumber() {
        return arrayRandomValuePicker(new Integer[]{321, 123});
    }

    default Supplier<PostCode> getDefaultPostCode() {
        return () -> aPostCode()
                .withOuter("AA1")
                .withInner(getNumber().value() < 200 ? "1AA" : "2AA")
                .build();
    }
}
