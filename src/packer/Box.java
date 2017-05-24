package packer;

import java.util.ArrayList;

public class Box {
	Vector dimension;
	ArrayList<Part> parts;
	public Box(Integer  l, Integer  w, Integer h) {
		dimension = new Vector(l, w, h);
		parts = new ArrayList<>();
	}
	public Box(Vector c) {
		dimension = c;
		parts = new ArrayList<>();
	}
	public String toString() {
		return dimension.toString() + "\n" + parts.toString();
	}
}
