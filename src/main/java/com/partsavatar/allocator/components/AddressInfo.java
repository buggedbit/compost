package com.partsavatar.allocator.components;

import com.easypost.model.Address;
import com.partsavatar.allocator.api.easyPost.EasyPostAPI;

import lombok.*;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"raw"})
@ToString(of = {"raw"})
public class AddressInfo {
    @NonNull
    private String raw;
    private Address easypostAddress;
    
    public void initEasyPostAddress(String id) {
    	String[] address = raw.split(",");
    	String[] state = address[2].split("-");
    	try {
    		easypostAddress = EasyPostAPI.getAddress(id, address[0], address[1], state[1], state[2] );
		} 
    	catch (IndexOutOfBoundsException e) {
			System.err.println("Wrong Syntax of address");
		}
    }
    
    public AddressInfo(AddressInfo address) {
        this.raw = address.raw;
        if(address.easypostAddress == null);
        	this.initEasyPostAddress(null);
    }
    
}
