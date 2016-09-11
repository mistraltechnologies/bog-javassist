package com.mistraltech.bog.examples.matchers;

import com.mistraltech.smog.core.annotation.Matches;
import com.mistraltech.bog.examples.model.Phone;
import org.hamcrest.Matcher;

@Matches(value = Phone.class, description = "a Phone")
public interface PhoneMatcher extends Matcher<Phone> {
    PhoneMatcher hasCode(String code);

    PhoneMatcher hasCode(Matcher<? super String> codeMatcher);

    PhoneMatcher hasNumber(String number);

    PhoneMatcher hasNumber(Matcher<? super String> numberMatcher);
}
