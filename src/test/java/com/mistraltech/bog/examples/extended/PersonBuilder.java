package com.mistraltech.bog.examples.extended;

import com.mistraltech.bog.core.Builder;
import com.mistraltech.bog.core.BuilderProperty;
import com.mistraltech.bog.core.annotation.Builds;
import com.mistraltech.bog.core.annotation.ConstructorParameter;
import com.mistraltech.bog.examples.model.Address;
import com.mistraltech.bog.examples.model.Person;
import com.mistraltech.bog.examples.model.Phone;

import java.util.List;

import static com.mistraltech.bog.proxy.javassist.JavassistBuilderGenerator.builderOf;

@Builds(Person.class)
public interface PersonBuilder extends AddresseeBuilder<PersonBuilder, Person> {
    static PersonBuilder aPerson() {
        return builderOf(PersonBuilder.class);
    }

    PersonBuilder from(Person person);

    @ConstructorParameter(0)
    PersonBuilder withName(String name);

    @ConstructorParameter(1)
    PersonBuilder withAge(int age);

    @ConstructorParameter(2)
    PersonBuilder withAddress(Address address);

    PersonBuilder withAddress(Builder<? extends Address> address);

    PersonBuilder withPhones(List<Phone> phones);


    BuilderProperty<String> getName();

    BuilderProperty<Integer> getAge();

    BuilderProperty<Address> getAddress();
}
