package com.partsavatar.allocator.api.easyPost;

import com.easypost.EasyPost;
import com.easypost.exception.EasyPostException;
import com.easypost.model.Address;
import com.easypost.model.Parcel;
import com.easypost.model.Rate;
import com.easypost.model.Shipment;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EasyPostAPI {
	@Getter private static final String[] CARRIERS = {"ExpeditedParcel","PurolatorGround"};
	private static final String PRODUCTION_KEY = "MBeHR0wTmyvsqxabxbCNFA";
	private static final String COUNTRY = "CANADA";
	
    public static Address getAddress(String name, String street, String city, String state, String zip){
    	EasyPost.apiKey = PRODUCTION_KEY; 
    	Map<String, Object> addressMap = new HashMap<String, Object>();
        addressMap.put("name", name);
        addressMap.put("street1", street);
        addressMap.put("city", city);
        addressMap.put("state", state);
        addressMap.put("zip", zip);
        addressMap.put("country", COUNTRY);
        try {
        	return Address.create(addressMap);
		} catch (EasyPostException e) {
			e.printStackTrace();
			return null;
		}
        
    }

    public static Parcel getParcel(Double weight, Integer height, Integer length, Integer width){
    	EasyPost.apiKey = PRODUCTION_KEY; 
    	Map<String, Object> parcelMap = new HashMap<String, Object>();
        parcelMap.put("weight", weight);
        parcelMap.put("height", height);
        parcelMap.put("width", width);
        parcelMap.put("length", length);
        try {
            return Parcel.create(parcelMap);
		} catch (EasyPostException e) {
			e.printStackTrace();
			return null;
		}
    }

    public static Shipment getShipment(Address toAddress, Address fromAddress, Parcel parcel){
    	EasyPost.apiKey = PRODUCTION_KEY; 
    	Map<String, Object> shipmentMap = new HashMap<>();
        shipmentMap.put("to_address", toAddress);
        shipmentMap.put("from_address", fromAddress);
        shipmentMap.put("parcel", parcel);
        try {
        	return Shipment.create(shipmentMap);
		} catch (EasyPostException e) {
			e.printStackTrace();
			return null;
		}
    }
    public static Map<String,Float> getRate(Shipment shipment) {
    	EasyPost.apiKey = PRODUCTION_KEY; 
    	Map<String, Float> rateMap = new HashMap<>(); 
    	List<Rate> rates = shipment.getRates();
    	for (Rate rate : rates) {
    		for (String service : CARRIERS) {
				if(rate.getService().equals(service))
					rateMap.put(rate.getCarrier(), rate.getRate());
    		}
		}
    	return rateMap;
    }
    
    public static void main(String[] args) throws EasyPostException {
        

        Address warehouseAddress = getAddress("McKesson", "71 Glacier St", "Coquitlam", "BC", "V3K5Z1");
        Address deliveryAddress = getAddress("RECEIVER", "11754 170 St NW", "Edmonton", "AB", "T5S1J7");
        Parcel parcel = getParcel(22.9, 12, 19, 8);

        Shipment shipment = getShipment(warehouseAddress, deliveryAddress, parcel);
        Map<String, Float> rateMap = getRate(shipment);
        for (String carrier : rateMap.keySet()) {
			System.out.println(carrier + rateMap.get(carrier));
		}
    }
}