package com.mistraltech.bog.examples.matchers;

import com.mistraltech.bog.examples.model.Address;
import com.mistraltech.bog.examples.model.PostCode;
import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

@Matches(value = Address.class, description = "an Address")
public interface AddressMatcher extends Matcher<Address> {
    AddressMatcher hasNumber(Integer number);

    AddressMatcher hasNumber(Matcher<? super Integer> numberMatcher);

    AddressMatcher hasPostCode(PostCode postCode);

    AddressMatcher hasPostCode(Matcher<? super PostCode> postCodeMatcher);
}
