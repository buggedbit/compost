package com.partsavatar.components;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class Part {
    @NonNull
    String sku;
    @NonNull
    int quantity;
    @NonNull
    int weight;
    @NonNull
    Dimension dimension;

    @Data
    private class Dimension {
        @NonNull
        int x;
        @NonNull
        int y;
        @NonNull
        int z;
    }

}
