package com.mistraltech.bog.examples.matchers;

import com.mistraltech.bog.examples.model.Address;
import com.mistraltech.bog.examples.model.Addressee;
import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

@Matches(value = Addressee.class, description = "an Addressee")
public interface AddresseeMatcher<R extends AddresseeMatcher, T extends Addressee> extends Matcher<T> {
    R hasName(String name);

    R hasName(Matcher<? super String> nameMatcher);

    R hasAddress(Address address);

    R hasAddress(Matcher<? super Address> addressMatcher);
}
