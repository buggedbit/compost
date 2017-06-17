package com.partsavatar.packer.dao.packing;

import com.partsavatar.packer.components.Box;
import com.partsavatar.packer.components.Order;

import java.io.IOException;
import java.util.List;


public interface PackingDAO {

    public List<Box> getPacking(List<Box> availableBoxes, Order order);

    public List<Box> getAvailableBoxes() throws NumberFormatException, IOException;

    public Order getNewOrder();

    public void storePacking(List<Box> filledBoxes);
}
