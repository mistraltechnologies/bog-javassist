package com.mistraltech.bog.examples.simple;

import com.mistraltech.bog.examples.model.Address;
import com.mistraltech.bog.examples.model.Person;
import com.mistraltech.bog.examples.model.PostCode;
import com.mistraltech.bog.examples.simple.builders.BadBuilder1;
import com.mistraltech.bog.examples.simple.builders.BadBuilder2;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Collections;

import static com.mistraltech.bog.examples.matchers.MatcherFactory.aPersonThat;
import static com.mistraltech.bog.examples.matchers.MatcherFactory.aPhoneThat;
import static com.mistraltech.bog.examples.matchers.MatcherFactory.aPostCodeThat;
import static com.mistraltech.bog.examples.matchers.MatcherFactory.anAddressThat;
import static com.mistraltech.bog.examples.simple.builders.AddressBuilder.anAddress;
import static com.mistraltech.bog.examples.simple.builders.PersonBuilder.aPerson;
import static com.mistraltech.bog.examples.simple.builders.PhoneBuilder.aPhone;
import static com.mistraltech.bog.examples.simple.builders.PostCodeBuilder.aPostCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
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
                .withPhones(Collections.singletonList(aPhone().withCode("01234").withNumber("123123").build()))
                .build();

        assertThat(person, is(aPersonThat()
                .hasName("Bob")
                .hasAge(21)
                .hasAddress(anAddressThat()
                        .hasNumber(321)
                        .hasPostCode(aPostCodeThat()
                                .hasOuter(OUTER)
                                .hasInner(INNER)))
                .hasPhones(Matchers.contains(aPhoneThat()
                        .hasCode("01234")
                        .hasNumber("123123")))));
    }

    @Test
    public void buildsWithDefaults() {
        Person person = aPerson()
                .build();

        assertThat(person, is(aPersonThat().hasName(nullValue()).hasAge(0).hasPhones(nullValue())));
    }

    @Test
    public void buildsFromTemplate() {
        Person p1 = aPerson()
                .withName("Ben")
                .withAge(81)
                .withAddress(anAddress()
                        .withNumber(45))
                .withPhones(Collections.singletonList(aPhone()
                        .withCode("01234")
                        .withNumber("567567")
                        .build()))
                .build();


        assertThat(p1, is(aPersonThat()
                .hasName("Ben")
                .hasAge(81)
                .hasAddress(anAddressThat()
                        .hasNumber(45))
                .hasPhones(contains(aPhoneThat()
                        .hasCode("01234")
                        .hasNumber("567567")))));

        Person p2 = aPerson().from(p1).build();

        assertThat(p2, is(aPersonThat()
                .hasName("Ben")
                .hasAge(81)
                .hasAddress(anAddressThat()
                        .hasNumber(45))
                .hasPhones(contains(aPhoneThat()
                        .hasCode("01234")
                        .hasNumber("567567")))));
    }

    @Test(expected = RuntimeException.class)
    public void cannotRepeatConstructorParameterIndex() {
        BadBuilder1.aBox().build();
    }

    @Test(expected = RuntimeException.class)
    public void cannotSpecifyNegativeConstructorParameterIndex() {
        BadBuilder2.aBox().build();
    }
}
