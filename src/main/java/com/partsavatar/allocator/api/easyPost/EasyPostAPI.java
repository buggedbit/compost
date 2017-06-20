package com.partsavatar.allocator.api.easyPost;

import com.easypost.EasyPost;
import com.easypost.exception.EasyPostException;
import com.easypost.model.Address;
import com.easypost.model.Parcel;
import com.easypost.model.Rate;
import com.easypost.model.Shipment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EasyPostAPI {
	private static String productionKey = "MBeHR0wTmyvsqxabxbCNFA";
	private static String[] carriers = {"ExpeditedParcel","PurolatorGround"};
	
    public static Address getAddress(String name, String street, String city, String state, String zip) throws EasyPostException {
        Map<String, Object> addressMap = new HashMap<String, Object>();
        addressMap.put("name", name);
        addressMap.put("street1", street);
        addressMap.put("city", city);
        addressMap.put("state", state);
        addressMap.put("zip", zip);
        addressMap.put("country", "Canada");
        return Address.create(addressMap);
    }

    public static Parcel getParcel(Double weight, Double height, Double length, Double width) throws EasyPostException {
        Map<String, Object> parcelMap = new HashMap<String, Object>();
        parcelMap.put("weight", 22.9);
        parcelMap.put("height", 12.1);
        parcelMap.put("width", 8);
        parcelMap.put("length", 19.8);
        return Parcel.create(parcelMap);
    }

    public static Shipment getShipment(Address toAddress, Address fromAddress, Parcel parcel) throws EasyPostException {
        Map<String, Object> shipmentMap = new HashMap<>();
        shipmentMap.put("to_address", toAddress);
        shipmentMap.put("from_address", fromAddress);
        shipmentMap.put("parcel", parcel);
        return Shipment.create(shipmentMap);
    }
    public static Map<String,Float> getRate(Shipment shipment) {
    	Map<String, Float> rateMap = new HashMap<>(); 
    	List<Rate> rates = shipment.getRates();
    	for (Rate rate : rates) {
    		for (String service : carriers) {
				if(rate.getService().equals(service))
					rateMap.put(rate.getCarrier(), rate.getRate());
    		}
		}
    	return rateMap;
    }
    
    public static void main(String[] args) throws EasyPostException {
        EasyPost.apiKey = productionKey; 

        Address warehouseAddress = getAddress("McKesson", "71 Glacier St", "Coquitlam", "BC", "V3K5Z1");
        Address deliveryAddress = getAddress("RECEIVER", "11754 170 St NW", "Edmonton", "AB", "T5S1J7");
        Parcel parcel = getParcel(22.9, 12.1, 19.8, 8.0);

        Shipment shipment = getShipment(warehouseAddress, deliveryAddress, parcel);
        Map<String, Float> rateMap = getRate(shipment);
        for (String carrier : rateMap.keySet()) {
			System.out.println(carrier + rateMap.get(carrier));
		}
    }
}