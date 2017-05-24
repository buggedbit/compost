package packer;

public class Surface {
	Vector leftUpperBehind; 
	Vector surface;
	public Surface(Vector leftBottomBehind, Vector partDim) {
		leftUpperBehind = new Vector(leftBottomBehind.x, leftBottomBehind.y + partDim.y, leftBottomBehind.z);
		surface = new Vector(partDim.x, 0, partDim.z);
	}
}
