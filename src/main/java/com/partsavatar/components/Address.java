package com.partsavatar.components;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class Address {
    @NonNull
    private String raw;

    public Address(Address address) {
        this.raw = address.raw;
    }
}
