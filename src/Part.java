import lombok.Data;
import lombok.NonNull;

public @Data class Part {
	@NonNull String id;
	@NonNull Integer quantity;
	@NonNull Integer weight;
	@NonNull Dimension dimesnion;
	
	public @Data class Dimension{
		@NonNull Integer x, y, z;	
	}

}
