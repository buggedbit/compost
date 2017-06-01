package packer;
import java.util.*;

public class Vector {
	public Integer x, y, z;
    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Vector)) {
            return false;
        }

        Vector v = (Vector) o;

        return v.x == x && v.y == y && v.z == z;
    }
	
    public Vector(Integer x, Integer  y, Integer  z){
		this.x = x; this.y = y; this.z = z;
	}
	public String toString(){
		return "x:" + x.toString() + " y:" + y.toString() +  " z:" + z.toString();
	}
	
	ArrayList<Direction> sort(){
		Integer max = x, middle = y, min = z;
		ArrayList<Direction> tmp = new ArrayList<>();
		tmp.add(new Direction('x', max));tmp.add(new Direction('y', middle));tmp.add(new Direction('z', min));
		Collections.sort(tmp,new Comparator<Direction>() {
			@Override
	        public int compare(Direction d1, Direction d2) {
				if(d1.val < d2.val)
					return 1;
				else if (d1.val > d2.val)
					return -1;
				else
					return 0;
	        }
	    });
		return tmp; 
	}
	public boolean rotateAndCheckIsEqualOrGreater(Part p){
		ArrayList<Direction> tmpPart = p.dimension.sort();
		ArrayList<Direction> tmpBox = this.sort();
		
		if(tmpBox.get(0).val >= tmpPart.get(0).val && tmpBox.get(1).val >= tmpPart.get(1).val && tmpBox.get(2).val >= tmpPart.get(2).val){
			
			if(tmpBox.get(0).c.equals('x'))
				p.dimension.x = tmpPart.get(0).val;
			else if(tmpBox.get(0).c.equals('y'))
				p.dimension.y = tmpPart.get(0).val;
			else if(tmpBox.get(0).c.equals('z'))
				p.dimension.z = tmpPart.get(0).val;
			
			if(tmpBox.get(1).c.equals('x'))
				p.dimension.x = tmpPart.get(1).val;
			else if(tmpBox.get(1).c.equals('y'))
				p.dimension.y = tmpPart.get(1).val;
			else if(tmpBox.get(1).c.equals('z'))
				p.dimension.z = tmpPart.get(1).val;
			
			if(tmpBox.get(2).c.equals('x'))
				p.dimension.x = tmpPart.get(2).val;
			else if(tmpBox.get(2).c.equals('y'))
				p.dimension.y = tmpPart.get(2).val;
			else if(tmpBox.get(2).c.equals('z'))
				p.dimension.z = tmpPart.get(2).val;
			
			return true;
		}
		if(x >= p.dimension.x && y >= p.dimension.y && z >= p.dimension.z )
			return true;
		else
			return false;
	}
	
	public boolean checkIsEqualOrGreater(Part p){
		if(x >= p.dimension.x && y >= p.dimension.y && z >= p.dimension.z )
			return true;
		else
			return false;
	}
	
	static Integer getContribution(Integer box, Integer part, Integer minPart){
		Integer cost = 0;
		if(box - part > minPart && box - part < 2*minPart)
			cost+=25;
		else if(box - part > 2*minPart && box - part < 5*minPart)
			cost+=35;
		else if(box - part > 5*minPart)
			cost+=50;
		else if(box - part == 0)
			cost+=100;
		return cost;
	}
	static Integer calculateCost(ArrayList<Direction> inp, Integer minPart){
		if(inp.get(0).val >= inp.get(3).val && inp.get(1).val >= inp.get(4).val && inp.get(2).val >= inp.get(5).val)
			return getContribution(inp.get(0).val, inp.get(3).val, minPart) +
			getContribution(inp.get(1).val, inp.get(4).val, minPart) +
			getContribution(inp.get(2).val, inp.get(5).val, minPart);		
		return -1;
	}
	ArrayList<Direction> getBestOrientation(Integer minPart, Part p){
		ArrayList<Direction> tmpPart = p.dimension.sort();
		ArrayList<Direction> tmpBox = this.sort();
		ArrayList<Direction> out = null;
		Integer maxCost = -1;
		for (int i = 0; i < 3; i++) {
			ArrayList<Direction> tmp1 = new ArrayList<>();
			tmp1.addAll(Arrays.asList(tmpBox.get(0),tmpBox.get(1),tmpBox.get(2),tmpPart.get(i),tmpPart.get((i+1)%3),tmpPart.get((i+2)%3)));
			Integer tmpCost = calculateCost(tmp1,minPart);
			if(tmpCost > maxCost){
				maxCost = tmpCost;
				out = tmp1;
			}
			ArrayList<Direction> tmp2 = new ArrayList<>();
			tmp2.addAll(Arrays.asList(tmpBox.get(0),tmpBox.get(1),tmpBox.get(2),tmpPart.get(i),tmpPart.get((i+2)%3),tmpPart.get((i+1)%3)));
			tmpCost = calculateCost(tmp2,minPart);
			if(tmpCost > maxCost){
				maxCost = tmpCost;
				out = tmp2;
			}
		}
		return out;
	}
	public boolean bestRotateAndCheckIsEqualOrGreater(Integer minPart, Part p){
		ArrayList<Direction>  req = getBestOrientation(minPart, p);
		if(req == null)
			return false;
		
		if(req.get(0).c.equals('x'))
			p.dimension.x = req.get(3).val;
		else if(req.get(0).c.equals('y'))
			p.dimension.y = req.get(3).val;
		else if(req.get(0).c.equals('z'))
			p.dimension.z = req.get(3).val;
		
		if(req.get(1).c.equals('x'))
			p.dimension.x = req.get(4).val;
		else if(req.get(1).c.equals('y'))
			p.dimension.y = req.get(4).val;
		else if(req.get(1).c.equals('z'))
			p.dimension.z = req.get(4).val;
		
		if(req.get(2).c.equals('x'))
			p.dimension.x = req.get(5).val;
		else if(req.get(2).c.equals('y'))
			p.dimension.y = req.get(5).val;
		else if(req.get(2).c.equals('z'))
			p.dimension.z = req.get(5).val;
		
		return true;
	}
}
