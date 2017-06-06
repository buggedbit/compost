package Coordinates;

/**
 * Represents a latitude on earth
 */
public class Longitude {

    /**
     * Degrees
     */
    int degrees;
    /**
     * Minutes
     */
    int minutes;
    /**
     * Seconds
     */
    double seconds;
    /**
     * Direction
     * True  -> East
     * False -> West
     */
    boolean direction;

    public Longitude(int degrees, int minutes, double seconds, boolean direction) {
        this.degrees = degrees;
        this.minutes = minutes;
        this.seconds = seconds;
        this.direction = direction;
    }

}
