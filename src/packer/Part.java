package packer;

public class Part {
	String id;
	Integer weight;
	Integer quantity;
	Vector position, dimension;
	
	public Part(String s, Integer l, Integer  w, Integer  h, Integer  wt, Integer qty) {
		id = s;
		
		dimension = new Vector(l, w, h);		
		position = new Vector(0, 0, 0);
		
		weight = wt;
		quantity = qty;
	}
	public Part(Part p, Vector pos, Integer qty) {
		id = p.id;
		
		dimension = new Vector(p.dimension.x, p.dimension.y, p.dimension.z);		
		position = new Vector(pos.x, pos.y, pos.z);
		
		weight = p.weight;
		quantity = qty;
	}
	public int hwlCompareTo(Part p) {
	    Integer lessThan = 1;
	    Integer greaterThan = -1;
	    
		if(this.dimension.y < p.dimension.y)
	    	return lessThan;
	    else if(this.dimension.y > p.dimension.y)
	    	return greaterThan;
	    else{
	    	if(this.dimension.z < p.dimension.z)
	    		return lessThan;	
	    	else if(this.dimension.z > p.dimension.z)
	    		return greaterThan;
	    	else{
	    		if (this.dimension.x < p.dimension.x)
	    	        return lessThan;
	    		else if (this.dimension.x > p.dimension.x)
	    	        return greaterThan;
	    		else
	    			return 0;
	    	}
	    }
	}
	public Integer getVol(){
		return dimension.x*dimension.y*dimension.z*quantity;
	}
	public int volCompareTo(Part p) {
	    Integer lessThan = 1;
	    Integer greaterThan = -1;
	    
		if(this.getVol() < p.getVol())
	    	return lessThan;
	    else if(this.getVol() > p.getVol())
	    	return greaterThan;
	    else
	    	return 0;
	}
	
	public String toString() {
		return dimension.toString() + " qty:" + quantity.toString() + "\t" +  position.toString() + "\n" ;
	}
}
