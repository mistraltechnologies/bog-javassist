package com.mistraltech.bog.examples.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Person extends Addressee {
    private int age;
    private List<Phone> phones;

    public Person(String name, int age, Address address) {
        super(name, address);
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                ", phones=" + phones +
                '}';
    }
}
