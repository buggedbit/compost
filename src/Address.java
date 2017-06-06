/**
 * Address on earth
 */
public class Address {
    /**
     * Raw verbal form of an address
     */
    String raw;

    public Address(String raw) {
        this.raw = raw;
    }

    public Address(Address address) {
        this.raw = address.raw;
    }
}
