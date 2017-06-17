package com.partsavatar.api.easyPost;

import java.util.HashMap;
import java.util.Map;

import com.easypost.EasyPost;
import com.easypost.exception.EasyPostException;
import com.easypost.model.Address;
import com.easypost.model.Parcel;
import com.easypost.model.Shipment;

public class EasyPostAPI {
	
	public static Address getAddress(String name, String street, String city, String state, String zip) throws EasyPostException{
		Map<String, Object> addressMap = new HashMap<String, Object>();
		addressMap.put("name", name);
		addressMap.put("street1", street);
        addressMap.put("city", city);
        addressMap.put("state", state);
        addressMap.put("zip", zip);
        addressMap.put("country", "Canada");
        return Address.create(addressMap);
	}
	public static Parcel getParcel(Double weight, Double height, Double length, Double width) throws EasyPostException{
		Map<String, Object> parcelMap = new HashMap<String, Object>();
        parcelMap.put("weight", 22.9);
        parcelMap.put("height", 12.1);
        parcelMap.put("width", 8);
        parcelMap.put("length", 19.8);
        return Parcel.create(parcelMap);
	}
	public static Shipment getShipment(Address toAddress, Address fromAddress, Parcel parcel) throws EasyPostException{
		Map<String, Object> shipmentMap = new HashMap<String, Object>();
		shipmentMap.put("to_address", toAddress);
        shipmentMap.put("from_address", fromAddress);
        shipmentMap.put("parcel", parcel);
        return Shipment.create(shipmentMap);
	}
    public static void main(String[] args) throws EasyPostException {
     	EasyPost.apiKey = "MBeHR0wTmyvsqxabxbCNFA"; //PartsAvatar

     	Address warehouseAddress = getAddress("McKesson", "71 Glacier St", "Coquitlam","BC","V3K5Z1");
        Address deliveryAddress = getAddress("RECEIVER", "11754 170 St NW","Edmonton", "AB", "T5S1J7");
        Parcel parcel = getParcel(22.9, 12.1, 19.8, 8.0);
        
        Shipment shipment = getShipment(warehouseAddress, deliveryAddress, parcel);
        System.out.println(shipment.prettyPrint());
    }
}