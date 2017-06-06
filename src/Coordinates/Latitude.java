package Coordinates;

/**
 * Represents a latitude on earth
 */
public class Latitude {

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
     * True  -> North
     * False -> South
     */
    boolean direction;

    public Latitude(int degrees, int minutes, double seconds, boolean direction) {
        this.degrees = degrees;
        this.minutes = minutes;
        this.seconds = seconds;
        this.direction = direction;
    }

}
