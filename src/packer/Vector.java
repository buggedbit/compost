package packer;

public class Vector {
	public Integer x, y, z;
	public Vector(Integer x, Integer  y, Integer  z){
		this.x = x; this.y = y; this.z = z;
	}
	public String toString(){
		return "x:" + x.toString() + " y:" + y.toString() +  " z:" + z.toString();
	}
	public boolean isEqualOrGreater(Vector c){
		if(x >= c.x && y >= c.y && z >= c.z)
			return true;
		else
			return false;
	}
}

