package com.mistraltech.bog.examples.matchers;

import com.mistraltech.bog.examples.model.Person;
import com.mistraltech.bog.examples.model.Phone;
import com.mistraltech.smog.core.annotation.Matches;
import com.mistraltech.smog.core.annotation.MatchesProperty;
import org.hamcrest.Matcher;

import java.util.List;

@Matches(value = Person.class, description = "a Person")
public interface PersonMatcher extends AddresseeMatcher<PersonMatcher, Person> {

    PersonMatcher hasAge(int yearsOld);

    PersonMatcher hasAge(Matcher<? super Integer> yearsOldMatcher);

    // Equivalent to hasAge...

    @MatchesProperty("age")
    PersonMatcher havingYearsOld(int yearsOld);

    @MatchesProperty("age")
    PersonMatcher havingYearsOld(Matcher<? super Integer> yearsOldMatcher);

    PersonMatcher hasPhoneList(Matcher<? super List<? extends Phone>> phoneListMatcher);
}
