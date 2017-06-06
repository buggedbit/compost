import Coordinates.Latitude;
import Coordinates.Longitude;

/**
 * Represents position on earth
 */
public class Address {
    /**
     * Latitude
     */
    Latitude latitude;
    /**
     * Longitude
     */
    Longitude longitude;

    public Address(Latitude latitude, Longitude longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
