package com.partsavatar.packer.components;


import lombok.Getter;
import lombok.ToString;

@ToString()
public class Surface {
	@Getter Vector leftUpperBehind; 
	@Getter Vector surface;
	
	public Surface(Vector leftBottomBehind, Vector partDim) {
		leftUpperBehind = new Vector(leftBottomBehind.getX(), leftBottomBehind.getY() + partDim.getY(), leftBottomBehind.getZ());
		surface = new Vector(partDim.getX(), 0, partDim.getZ());
	}
}
