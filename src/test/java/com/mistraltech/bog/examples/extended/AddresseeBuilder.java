package com.mistraltech.bog.examples.extended;

import com.mistraltech.bog.core.Builder;
import com.mistraltech.bog.core.BuilderProperty;
import com.mistraltech.bog.core.TwoPhaseBuilder;
import com.mistraltech.bog.core.annotation.Builds;
import com.mistraltech.bog.core.annotation.ConstructorParameter;
import com.mistraltech.bog.examples.model.Address;
import com.mistraltech.bog.examples.model.Addressee;
import com.mistraltech.bog.examples.model.Person;
import com.mistraltech.bog.examples.model.Phone;

import java.util.List;

import static com.mistraltech.bog.proxy.javassist.JavassistBuilderGenerator.builderOf;

@Builds(Addressee.class)
public interface AddresseeBuilder<R extends AddresseeBuilder<R, T>, T extends Addressee> extends TwoPhaseBuilder<T> {
    @SuppressWarnings("unchecked")
    static AddresseeBuilder<?, Addressee> anAddressee() {
        return builderOf(AddresseeBuilder.class);
    }

    AddresseeBuilder from(Addressee addressee);

    @ConstructorParameter(0)
    R withName(String name);

    @ConstructorParameter(1)
    R withAddress(Address address);

    R withAddress(Builder<? extends Address> address);

    BuilderProperty<String> getName();

    BuilderProperty<Address> getAddress();
}
