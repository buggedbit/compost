package com.partsavatar.allocator.components.warehouse;

import com.partsavatar.allocator.components.Address;
import com.partsavatar.allocator.estimates.Estimate;
import com.partsavatar.packer.components.Part;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Assert if warehouse has a product it definitely contains that product's cost price and clone count
 */
@Getter
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
public class Warehouse {

    @NonNull
    private String id;
    @NonNull
    private Address address;

    public Warehouse(@NonNull final String id, @NonNull final Address address) {
        this.id = id;
        this.address = address;
    }

    public static Vector<Warehouse> getAll() {
        return new WarehouseDAOImpl().getAll();
    }

    public ProductInfo getProductInfo(@NonNull final String sku) {
        return new WarehouseDAOImpl().getProductInfo(this.id, sku);
    }

    public boolean containsProduct(@NonNull final String sku) {
        return this.getProductInfo(sku) != null;
    }

    List<String> available = new ArrayList<>();

    public void addToAvailable(String s) {
        available.add(s);
    }

    public void removeFromAvailable(String s) {
        available.remove(s);
    }

    public void descendingSort() {
        available.sort((o1, o2) -> {
            Part p1 = Estimate.estimatePart(o1, 1);
            Part p2 = Estimate.estimatePart(o2, 1);
            return p2.volCompareTo(p1);
        });
    }

    public void ascendingSort() {
        available.sort((o1, o2) -> {
            Part p1 = Estimate.estimatePart(o1, 1);
            Part p2 = Estimate.estimatePart(o2, 1);
            return p1.volCompareTo(p2);
        });
    }
}
