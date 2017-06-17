package com.partsavatar.estimates;

import com.partsavatar.productdimension.ProductDimension;
import com.partsavatar.productdimension.ProductDimensionDAO;
import com.partsavatar.productdimension.ProductDimensionDAOImpl;

import components.Part;
import components.Vector;

public class Estimate {
	private static ProductDimensionDAO estimator = new ProductDimensionDAOImpl();  
	public static Part estimatePart(String sku, Integer qty) {
		ProductDimension estimate = estimator.getBySku(sku);
		return new Part(estimate.getSku(), 
				new Vector((int)estimate.getLength(), (int)estimate.getBreadth(),(int)estimate.getHeight()),
				(int)estimate.getWeight(), qty);
	}
}
