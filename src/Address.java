public class Address {
    String country, state, street;
	public Address(String c) {
		country = c;
	}
	public Address(String c,String s) {
		country = c;
		state = s;
	}
	public Address(String c, String s, String st) {
		country = c;
		state = s;
		street = st;
	}
	public String toString() {
		return street + ", " + state + ", " + country;
	}
}
