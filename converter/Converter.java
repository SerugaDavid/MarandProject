import com.mysql.cj.xdevapi.JsonArray;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Converter {
    public static void main(String[] args) {
        String url = "http://localhost:8080/api/flights";
        try {
            JSONArray airlines = getNewAirlines("./data_file.csv");
            APIPost(airlines, url + "/airlines");

            JSONArray airports = getNewAirports("./data_file.csv");
            APIPost(airports, url + "/airports");

            JSONArray flights = getFlights("./data_file.csv");
            APIPost(flights, url);

        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            e.printStackTrace();
        }
    }

    /**
     * Calls API to add airlines to database.
     * @param airlines JSONArray with all airlines.
     */
    public static void APIPost(JSONArray airlines, String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
        } catch (IOException e) {
            System.out.println("Malformed URL!");
            e.printStackTrace();
            System.exit(1);
        }
        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            System.out.println("Protocol exception!");
            e.printStackTrace();
            System.exit(1);
        }
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        connection.setDoOutput(true);
        try {
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(airlines.toString());
            writer.flush();
        } catch (IOException e) {
            System.out.println("IO exception! Writing Json");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            int responseCode = connection.getResponseCode();
            if (responseCode == 200)
                System.out.println("Post was successful!");
            else
                System.out.println("Post failed!");
        } catch (IOException e) {
            System.out.println("IO exception! Getting response code");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static JSONArray getFlights(String path) throws FileNotFoundException {
        return null;
    }

    /**
     * Get all new airlines from file.
     *
     * @param path Path to file.
     * @return JSONArray with all new airlines.
     * @throws FileNotFoundException If file is not found.
     */
    public static JSONArray getNewAirlines(String path) throws FileNotFoundException {
        HashSet<String[]> airlines = new HashSet<>();
        Scanner sc = new Scanner(new File(path));
        String line;
        String[] lineSplit;

        while (sc.hasNextLine()) {
            line = sc.nextLine().trim();
            lineSplit = line.split("\\^");
            airlines.add(new String[]{lineSplit[3], lineSplit[0].substring(0, 2)});
        }

        JSONArray name = new JSONArray();
        JSONArray tag = new JSONArray();
        for (String[] airline : airlines) {
            name.put(airline[0]);
            tag.put(airline[1]);
        }

        JSONArray out = new JSONArray();
        out.put(name);
        out.put(tag);

        return out;
    }

    /**
     * Get all new airports from file.
     * @param path Path to file.
     * @return JSONArray with all new airports.
     * @throws FileNotFoundException
     */
    public static JSONArray getNewAirports(String path) throws FileNotFoundException {
        HashSet<String> airports = new HashSet<>();
        Scanner sc = new Scanner(new File(path));
        String line;
        String[] lineSplit;

        while (sc.hasNextLine()) {
            line = sc.nextLine().trim();
            lineSplit = line.split("\\^");
            airports.add(lineSplit[1]);
            airports.add(lineSplit[2]);
        }

        JSONArray array = new JSONArray();
        for (String airport : airports) {
            array.put(airport);
        }

        return array;
    }
}
