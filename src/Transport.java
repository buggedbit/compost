

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Transport {
	String apiKey = "AIzaSyBBX4EhjaQrGzz7l6cnOf2e8zvrgPGas9E";
	String[] origins;
	String[] destinations;
	
	public Transport(String[] org, String[] dst) {
		origins = org;
		destinations = dst;
	}
	static ArrayList<Response> toJson(String s) throws Exception {
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject)parser.parse(s);
		if(obj.get("status").equals("OK")){
			ArrayList<String> origins = new ArrayList<>();
			JSONArray org = (JSONArray) obj.get("origin_addresses");
			for (Object object : org) {
				origins.add((String)object);
			}
			
			ArrayList<String> destinations = new ArrayList<>();
			JSONArray dst = (JSONArray) obj.get("destination_addresses");
			for (Object object : dst) {
				destinations.add((String)object);
			}
			
			ArrayList<Response> out = new ArrayList<>();
			JSONArray pairs = (JSONArray) obj.get("rows");	
			for (int i = 0; i < origins.size(); i++) {
				JSONArray tmpOrg = (JSONArray)((JSONObject) pairs.get(i)).get("elements");	
				for (int j = 0; j < destinations.size(); j++) {
					JSONObject tmpPair = (JSONObject) tmpOrg.get(j);
					if(tmpPair.get("status").equals("OK")){
						Long tmpDist =(long)((Object)((JSONObject)tmpPair.get("distance")).get("value"));
						Long tmpDuration =(long)((Object)((JSONObject)tmpPair.get("duration")).get("value"));
						out.add(new Response(origins.get(i), destinations.get(j),tmpDist.intValue(),tmpDuration.intValue()));
					}
				}
			}
			return out;
		}
		return null;
	}
	static String getHTML(String urlToRead) throws Exception {
		StringBuilder result = new StringBuilder();
		URL url = new URL(urlToRead);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return result.toString();
      
	}
	public ArrayList<Response> runAPI() throws Exception{
		String org = String.join("|", origins);
		String dst = String.join("|", destinations);
		String jsonStr = getHTML("https://maps.googleapis.com/maps/api/distancematrix/json?origins="+URLEncoder.encode(org, "UTF-8")+"&destinations="+URLEncoder.encode(dst, "UTF-8")+"&key=" + apiKey);
		return toJson(jsonStr);
	}
	
	public static void main(String[] args) throws Exception{
		
		String[] origins = {"Mumbai, India","Chennai","Nashik"};
		String[] destinations = {"Bidar"};
		Transport tmp = new Transport(origins, destinations);
		ArrayList<Response> responses = tmp.runAPI();
		System.out.println(responses.toString());
	}
}