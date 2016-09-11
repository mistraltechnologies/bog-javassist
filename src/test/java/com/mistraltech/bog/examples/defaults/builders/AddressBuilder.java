package com.mistraltech.bog.examples.defaults.builders;

import com.mistraltech.bog.core.Builder;
import com.mistraltech.bog.core.annotation.Builds;
import com.mistraltech.bog.core.picker.ValuePicker;
import com.mistraltech.bog.core.propertybuilder.ValueContainer;
import com.mistraltech.bog.examples.model.Address;
import com.mistraltech.bog.examples.model.PostCode;

import static com.mistraltech.bog.core.picker.ArrayValuePicker.arrayValuePicker;
import static com.mistraltech.bog.core.picker.SingleValuePicker.singleValuePicker;
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

    Integer getNumber();

    default ValuePicker<Integer> getDefaultNumber() { return arrayValuePicker(new Integer[] {321, 123}); }

    default ValuePicker<PostCode> getDefaultPostCode() {
        return () -> aPostCode()
                .withOuter("AA1")
                .withInner(getNumber() < 200 ? "1AA" : "2AA")
                .build();
    }
}
