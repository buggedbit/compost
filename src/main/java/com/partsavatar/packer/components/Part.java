package components;
import lombok.Data;
import lombok.NonNull;

public @Data class Part {
	@NonNull String id;
	@NonNull Vector dimension;
	@NonNull Integer weight;
	@NonNull Integer quantity;
	
	Vector position; 
	
	Integer getVol(){
		return dimension.getX()*dimension.getY()*dimension.getZ();
	}
	
	int volCompareTo(Part p) {
	    Integer lessThan = 1;
	    Integer greaterThan = -1;
	    
		if(this.getVol() < p.getVol())
	    	return lessThan;
	    else if(this.getVol() > p.getVol())
	    	return greaterThan;
	    else
	    	return 0;
	}
	public Part copy() {
		Part p = new Part(id, dimension, weight, quantity);
		p.position = position;
		return p;
	}
}
