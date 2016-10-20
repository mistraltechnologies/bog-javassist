package com.mistraltech.bog.examples.extended;

import com.mistraltech.bog.examples.model.Address;
import com.mistraltech.bog.examples.model.Person;
import org.junit.Test;

import static com.mistraltech.bog.examples.extended.PersonBuilder.aPerson;
import static com.mistraltech.bog.examples.matchers.MatcherFactory.aPersonThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExtendedBuilderExamplesTest {

    @Test
    public void canInstantiateBuilder() {
        Address address = new Address();
        Person bob = aPerson().withName("Bob").withAge(21).withAddress(address).build();

        assertThat(bob, is(aPersonThat().hasName("Bob").hasAge(21).hasAddress(address)));
    }
}
