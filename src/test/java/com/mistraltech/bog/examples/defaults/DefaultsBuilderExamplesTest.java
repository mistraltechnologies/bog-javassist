package com.mistraltech.bog.examples.defaults;

import com.mistraltech.bog.examples.model.Address;
import com.mistraltech.bog.examples.model.PostCode;
import org.hamcrest.Matcher;
import org.junit.Test;

import static com.mistraltech.bog.examples.defaults.builders.AddressBuilder.anAddress;
import static com.mistraltech.bog.examples.matchers.MatcherFactory.aPostCodeThat;
import static com.mistraltech.bog.examples.matchers.MatcherFactory.anAddressThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DefaultsBuilderExamplesTest {

    @Test
    public void canSupplyDefaultValuePicker() {
        Address address = anAddress().build();

        Integer number = address.getNumber();
        String innerCode = number < 200 ? "1AA" : "2AA";

        Matcher<PostCode> expectedPostCode = aPostCodeThat().hasOuter("AA1").hasInner(innerCode);

        assertThat(address, is(anAddressThat().hasPostCode(expectedPostCode)));
    }

}
