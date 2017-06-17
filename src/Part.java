import java.util.Set;

import lombok.Data;
import lombok.NonNull;

public @Data class Part {
	@NonNull String id;
	@NonNull Integer quantity;
	@NonNull Integer weight;
	@NonNull Dimension dimesnion;
	Set<Integer> availability;
	
	public @Data class Dimension{
		@NonNull Integer x, y, z;	
	}
	
	Integer getVol(){
		return dimesnion.x * dimesnion.y * dimesnion.z;
	}
	public int volCompare(Part p){
		if(this.getVol() < p.getVol())
			return -1;
		else if(this.getVol() == p.getVol())
			return 0;
		else
			return 1;
	}
	public int preferentialCompare(Part p) {
		if(availability.size() == 1){
			if(p.availability.size() != 1)
				return -1;
			else{
				return volCompare(p);
			}		
		}
		if(p.availability.size() == 1){
			if(availability.size() != 1)
				return 1;
			else{
				return volCompare(p);
			}
		}
		
		if(availability.size() == 2){
			if(p.availability.size() == 2)
				return volCompare(p);
			else
				return -1;
		}
		if(p.availability.size() == 2){
			if(availability.size() == 2)
				return volCompare(p);
			else
				return 1;
		}
				
		return getVol();
	}
}
