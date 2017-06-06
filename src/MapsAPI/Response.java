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

}
