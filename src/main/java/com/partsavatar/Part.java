package com.partsavatar;

import lombok.Data;
import lombok.NonNull;

@Data
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
    public class Dimension {
        @NonNull
        int x;
        @NonNull
        int y;
        @NonNull
        int z;
    }

}
