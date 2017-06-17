package components;


import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor()
@ToString()
public class Direction {
	Integer val;
	Character c;
	
	public int compareTo(Direction d){
		if(this.val < d.val)
			return 1;
		else if (this.val > d.val)
			return -1;
		else
			return 0;
	}

}
