package MapsAPI;

public class Response {
    public String origin;
    public String destination;
    public long distance;
    public long duration;

    public Response(String origin, String destination, long distance, long duration) {
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.duration = duration;
    }

    public String toString() {
        return "origin : " + origin + " \n" +
                "destination : " + destination + " \n" +
                "distance : " + distance + " m \n" +
                "duration : " + duration + " s \n";
    }
    public int durationCompare(Response r) {
        if (duration < r.duration) {
            return -1;
        } else if (duration == r.duration) {
            return 0;
        } else {
            return 1;
        }
    }
    public int distanceCompare(Response r) {
        if (distance < r.distance) {
            return -1;
        } else if (distance == r.distance) {
            return 0;
        } else {
            return 1;
        }
    }
    
}
