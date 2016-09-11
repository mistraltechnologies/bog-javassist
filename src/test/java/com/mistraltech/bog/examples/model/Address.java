package com.mistraltech.bog.examples.model;

public class Address {
    private Integer number;
    private PostCode postCode;

    public Address() {
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public PostCode getPostCode() {
        return postCode;
    }

    public void setPostCode(PostCode postCode) {
        this.postCode = postCode;
    }

    @Override
    public String toString() {
        return "Address{" +
                "houseNumber=" + number +
                ", postCode=" + postCode +
                '}';
    }
}
