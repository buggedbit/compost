package com.partsavatar.allocator.components;

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
