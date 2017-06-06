package MapsAPI.Google;

import MapsAPI.Google.Exceptions.StatusNotOKException;
import MapsAPI.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;

/**
 * Google Maps API manager
 */
public class GoogleMaps {

    private static final String API_KEY = "AIzaSyBBX4EhjaQrGzz7l6cnOf2e8zvrgPGas9E";

    private static String getRequestURL(String[] origins, String[] destinations) throws UnsupportedEncodingException {
        String org = String.join("|", origins);
        String dst = String.join("|", destinations);
        return ("https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + URLEncoder.encode(org, "UTF-8") + "&destinations=" + URLEncoder.encode(dst, "UTF-8") + "&key=" + API_KEY);
    }

    private static String performGETRequest(String url_s) throws IOException {
        // Prepare and open connection
        URL url = new URL(url_s);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Read response
        StringBuilder result = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(conn.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            result.append(line);
        }

        // Release resources
        isr.close();
        br.close();

        return result.toString();
    }

    private static ArrayList<Response> parseJSON(String json_string) throws ParseException, StatusNotOKException {

        JSONObject json_response = (JSONObject) new JSONParser().parse(json_string);

        if (json_response.get("status").equals("OK")) {
            ArrayList<String> origins = new ArrayList<>();
            JSONArray org = (JSONArray) json_response.get("origin_addresses");
            for (Object object : org) {
                origins.add((String) object);
            }

            ArrayList<String> destinations = new ArrayList<>();
            JSONArray dst = (JSONArray) json_response.get("destination_addresses");
            for (Object object : dst) {
                destinations.add((String) object);
            }

            ArrayList<Response> parsed_response = new ArrayList<>();
            JSONArray pairs = (JSONArray) json_response.get("rows");

            for (int i = 0; i < origins.size(); i++) {
                JSONArray ith_origin = (JSONArray) ((JSONObject) pairs.get(i)).get("elements");

                for (int j = 0; j < destinations.size(); j++) {
                    JSONObject ij_pair = (JSONObject) ith_origin.get(j);

                    if (ij_pair.get("status").equals("OK")) {
                        long ij_dist = (long) ((JSONObject) ij_pair.get("distance")).get("value");
                        long ij_duration = (long) ((JSONObject) ij_pair.get("duration")).get("value");
                        parsed_response.add(new Response(origins.get(i), destinations.get(j), ij_dist, ij_duration));
                    } else {
                        throw new StatusNotOKException();
                    }

                }

            }

            return parsed_response;
        } else {
            throw new StatusNotOKException();
        }

    }

    public static ArrayList<Response> getDistancesAndTimes(String[] origins, String[] destinations) throws IOException, ParseException, StatusNotOKException {
        String url_s = getRequestURL(origins, destinations);
        String json_string = performGETRequest(url_s);
        ArrayList<Response> parsed_response = parseJSON(json_string);
        return parsed_response;
    }

}
