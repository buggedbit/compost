package com.partsavatar.allocator.components;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = {"raw"})
@ToString(of = {"raw"})
public class Address {
    @NonNull
    private String raw;

    public Address(Address address) {
        this.raw = address.raw;
    }
}
