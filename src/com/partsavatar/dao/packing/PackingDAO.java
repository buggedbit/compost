package com.partsavatar.dao.packing;

import java.util.List;
import java.io.IOException;

import components.Box;
import components.Order;

public interface PackingDAO {
	
	public List<Box> getPacking(List<Box> availableBoxes, Order order);
	public List<Box> getAvailableBoxes()  throws NumberFormatException, IOException;
	public Order getNewOrder();
	
	public void storePacking(List<Box> filledBoxes);
}
