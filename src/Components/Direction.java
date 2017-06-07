package Components;
public class Direction {
	public Integer val;
	public Character c;
	public Direction(Character ch, Integer v) {
		val = v;
		c =ch;
	}
	public String toString(){
		return c.toString() + " " + val.toString() + "\n";
	}
}
