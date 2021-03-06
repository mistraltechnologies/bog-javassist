package com.mistraltech.bog.examples.matchers;

import static com.mistraltech.smog.proxy.javassist.JavassistMatcherGenerator.matcherOf;

public final class MatcherFactory {
    public static PersonMatcher aPersonThat() {
        return matcherOf(PersonMatcher.class);
    }

    public static AddresseeMatcher anAddresseeThat() {
        return matcherOf(AddresseeMatcher.class);
    }

    public static PhoneMatcher aPhoneThat() {
        return matcherOf(PhoneMatcher.class);
    }

    public static AddressMatcher anAddressThat() {
        return matcherOf(AddressMatcher.class);
    }

    public static PostCodeMatcher aPostCodeThat() {
        return matcherOf(PostCodeMatcher.class);
    }
}
