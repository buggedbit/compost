package com.partsavatar.allocator.components.warehouse;

import com.opencsv.CSVReader;
import com.partsavatar.allocator.components.Address;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class WarehouseDAOImpl implements WarehouseDAO {

    private static final String dbDir = "/home/pandu/Desktop/PartsAvatar/TakeItEasy/TakeItEasy/db/";

    private class Inventory {
        Map<String, Double> skuCostPriceMap = new HashMap<>();
        Map<String, Integer> skuCloneCount = new HashMap<>();

        public void set(String sku, double costPrice, int cloneCount) {
            this.skuCostPriceMap.put(sku, costPrice);
            this.skuCloneCount.put(sku, cloneCount);
        }
    }

    private static Map<Warehouse, Inventory> cachedData = new HashMap<>();

    private static boolean isCachingComplete = false;

    private boolean cache() {
        cachedData = new HashMap<>();

        try {
            final String allWarehousesPath = dbDir + "all.csv";

            CSVReader allReader = new CSVReader(new FileReader(allWarehousesPath));

            // Ignore CSV heading
            String[] wFields = allReader.readNext();
            // Reads Warehouse
            while ((wFields = allReader.readNext()) != null) {
                Warehouse warehouse = new Warehouse(wFields[0], new Address(wFields[1]));
                Inventory inventory = new Inventory();

                final String warehousesInventoryPath = dbDir + wFields[2];
                CSVReader ithReader = new CSVReader(new FileReader(warehousesInventoryPath));
                // Ignore CSV heading
                String[] iFields = ithReader.readNext();
                // Reads Inventory
                while ((iFields = ithReader.readNext()) != null) {
                    double costPrice = Double.parseDouble(iFields[1]);
                    int cloneCount = Integer.parseInt(iFields[2]);
                    inventory.set(iFields[0], costPrice, cloneCount);
                }
                cachedData.put(
                        warehouse,
                        inventory
                );
                ithReader.close();
            }
            allReader.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean ensureCachingIsComplete() {
        if (!isCachingComplete) {
            if (this.cache()) {
                isCachingComplete = true;
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public Vector<Warehouse> getAll() {
        if (ensureCachingIsComplete()) {
            Vector<Warehouse> ans = new Vector<>();
            ans.addAll(cachedData.keySet());
            return ans;
        } else return null;
    }

    @Override
    public ProductInfo getProductInfo(String warehouseId, String sku) {
        if (ensureCachingIsComplete()) {
            for (Map.Entry<Warehouse, Inventory> warehouseInventory : cachedData.entrySet()) {
                if (warehouseId.equals(warehouseInventory.getKey().getId())) {
                    Inventory inventory = warehouseInventory.getValue();

                    if (inventory.skuCloneCount.containsKey(sku) && inventory.skuCostPriceMap.containsKey(sku)) {
                        return new ProductInfo(sku, inventory.skuCostPriceMap.get(sku), inventory.skuCloneCount.get(sku));
                    } else {
                        return null;
                    }
                }
            }
            return null;
        } else {
            return null;
        }
    }

}
