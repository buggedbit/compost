package com.partsavatar;

public class Address {
    String raw;

    public Address(String raw) {
        this.raw = raw;
    }

    public Address(Address address) {
        this.raw = address.raw;
    }
}
