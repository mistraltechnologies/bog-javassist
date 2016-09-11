package com.mistraltech.bog.examples.simple;

import com.mistraltech.bog.examples.model.Address;
import com.mistraltech.bog.examples.model.Person;
import com.mistraltech.bog.examples.model.Phone;
import com.mistraltech.bog.examples.model.PostCode;
import org.hamcrest.Matchers;
import org.junit.Test;

import static com.mistraltech.bog.examples.simple.builders.AddressBuilder.anAddress;
import static com.mistraltech.bog.examples.simple.builders.PersonBuilder.aPerson;
import static com.mistraltech.bog.examples.simple.builders.PhoneBuilder.aPhone;
import static com.mistraltech.bog.examples.simple.builders.PostCodeBuilder.aPostCode;
import static com.mistraltech.bog.examples.matchers.MatcherFactory.aPersonThat;
import static com.mistraltech.bog.examples.matchers.MatcherFactory.aPhoneThat;
import static com.mistraltech.bog.examples.matchers.MatcherFactory.aPostCodeThat;
import static com.mistraltech.bog.examples.matchers.MatcherFactory.anAddressThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class SimpleBuilderExamplesTest {

    private static final String OUTER = "AA1";

    private static final String INNER = "1AA";

    @Test
    public void builderBuildsWithSpecifiedConstructorValues() {
        PostCode postCode = aPostCode()
                .withOuter(OUTER)
                .withInner(INNER)
                .build();

        assertThat(postCode, is(aPostCodeThat().hasOuter(OUTER).hasInner(INNER)));
    }

    @Test
    public void builderBuildsWithSpecifiedSetterValues() {
        PostCode postCode = aPostCode()
                .withOuter(OUTER)
                .withInner(INNER)
                .build();

        Address address = anAddress()
                .withNumber(21)
                .withPostCode(postCode)
                .build();

        assertThat(address, is(anAddressThat().hasNumber(21).hasPostCode(postCode)));
    }

    @Test
    public void builderBuildsWithBuilderAsValue() {

        Address address = anAddress()
                .withNumber(321)
                .withPostCode(aPostCode()
                        .withOuter(OUTER)
                        .withInner(INNER))
                .build();

        assertThat(address, is(anAddressThat()
                .hasNumber(321)
                .hasPostCode(aPostCodeThat()
                        .hasOuter(OUTER)
                        .hasInner(INNER))));
    }

    @Test
    public void buildsObjectGraph() {
        Person person = aPerson()
                .withName("Bob")
                .withAddress(anAddress()
                        .withNumber(321)
                        .withPostCode(aPostCode()
                                .withOuter(OUTER)
                                .withInner(INNER)))
                .withAge(21)
                .withPhones(new Phone[]{
                        aPhone().withCode("01234").withNumber("123123").build()
                })
                .build();

        assertThat(person, is(aPersonThat()
                .hasName("Bob")
                .hasAge(21)
                .hasAddress(anAddressThat()
                        .hasNumber(321)
                        .hasPostCode(aPostCodeThat()
                                .hasOuter(OUTER)
                                .hasInner(INNER)))
                .hasPhoneList(Matchers.contains(aPhoneThat()
                        .hasCode("01234")
                        .hasNumber("123123")))));
    }

    @Test
    public void buildsWithDefaults() {
        Person person = aPerson()
                .build();

        assertThat(person, is(aPersonThat().hasName(nullValue()).hasAge(0).hasPhoneList(nullValue())));
    }

    @Test
    public void cannotRepeatConstructorParameterIndex() {
        // TODO
    }

    @Test
    public void cannotSpecifyNegativeConstructorParameterIndex() {
        // TODO
    }
}
