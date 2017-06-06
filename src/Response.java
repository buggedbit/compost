public class Response {
	String origin, destination;
	Integer distance, duration;	
	public Response(String org,String dst,Integer d, Integer t) {
		origin = org;
		destination = dst;
		distance = d;
		duration = t;
	}
	Float toKm(){
		return (float)(distance/1000.0);
	}
	Float toHr(){
		return (float)(duration/3600.0);
	}
	public String toString() {
		return "ORG : " + origin.toString() + " \n" +
				"DST : " + destination.toString() + " \n" +
				"dist : " + toKm().toString() + " Km \n" +
				"time : " + toHr().toString() + " Hr \n";
	}
}
