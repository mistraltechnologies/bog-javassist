package com.mistraltech.bog.examples.simple.builders;

import com.mistraltech.bog.core.Builder;
import com.mistraltech.bog.core.annotation.Builds;
import com.mistraltech.bog.core.annotation.ConstructorParameter;
import com.mistraltech.bog.examples.model.Address;
import com.mistraltech.bog.examples.model.Person;
import com.mistraltech.bog.examples.model.Phone;

import static com.mistraltech.bog.proxy.javassist.JavassistBuilderGenerator.builderOf;

@Builds(Person.class)
public interface PersonBuilder extends Builder<Person> {
    static PersonBuilder aPerson() {
        return builderOf(PersonBuilder.class);
    }

    @ConstructorParameter(0)
    PersonBuilder withName(String name);

    @ConstructorParameter(1)
    PersonBuilder withAge(int age);

    @ConstructorParameter(2)
    PersonBuilder withAddress(Address address);

    PersonBuilder withAddress(Builder<Address> address);

    @ConstructorParameter(3)
    PersonBuilder withPhones(Phone[] phones);
}
