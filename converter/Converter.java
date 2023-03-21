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
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Converter {

    public static void main(String[] args) {
        String url = "http://localhost:8080/api/flights";
        String path = "C:\\Users\\david\\Documents\\GitHub\\MarandProject\\converter\\data_file.csv";
        try {
            // done

            JSONArray airlines = getNewAirlines(path);
            APIPost(airlines, url + "/airlines");

            JSONArray airports = getNewAirports(path);
            APIPost(airports, url + "/airports");

            JSONArray flights = getFlights(path, url);
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

    public static JSONArray getFlights(String path, String url) throws FileNotFoundException {
        // TODO: call API to get airport id-s
        // TODO: call API to get airline id-s

        // read file
        Scanner sc = new Scanner(new File(path));
        String line;
        String[] lineSplit;
        JSONArray flights = new JSONArray();
        JSONObject row;

        while (sc.hasNextLine()) {
            line = sc.nextLine().trim();
            lineSplit = line.split("\\^");
            row = new JSONObject();
            row.put("FlightNumber", lineSplit[0]);
            row.put("OriginAirportID", getId("airports", "abbreviation", lineSplit[1], url));
            row.put("DestinationAirportID", getId("airports", "abbreviation", lineSplit[2], url));
            row.put("Airlines_idAirlines", getId("airlines", "name", lineSplit[3], url));
            row.put("Price", Double.parseDouble(lineSplit[4]));
            row.put("Day", lineSplit[5]);
            row.put("Time", durationToTime(lineSplit[6]));
            row.put("Duration", durationToTime(lineSplit[7]));
            row.put("AvailableSeats", Integer.parseInt(lineSplit[8]));
            flights.put(row);
        }

        return flights;
    }

    /**
     * Converts duration to time value
     * @param duration duration of a flight in format 00h00m
     * @return String value of time
     */
    public static String durationToTime(String duration) {
        int hour = 0;
        int min = 0;
        if (duration.contains("m")) {
            if (duration.contains("h")) {
                String[] split = duration.split("h");
                hour = Integer.parseInt(split[0]);
                duration = split[1];
            }
            min = Integer.parseInt(duration.substring(0, duration.length()-1));
        } else if (duration.contains(":")) {
            String[] split = duration.split(":");
            hour = Integer.parseInt(split[0]);
            min = Integer.parseInt(split[1]);
        }
        hour += min / 60;
        min = min % 60;

        return hour + ":" + min + ":00";
    }

    /**
     * Gets id of an airline or an airport for a given name or abbreviation
     * @param table airline or airport
     * @param column name or abbreviation
     * @param parameter value for our column
     * @param url url of our api
     * @return id for given parameters
     */
    public static int getId(String table, String column, String parameter, String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url + "/" + table + "/" + column + "/" + parameter).openConnection();
        } catch (IOException e) {
            System.out.println("Malformed URL!");
            e.printStackTrace();
            System.exit(1);
        }
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            System.out.println("Protocol exception!");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            if (connection.getResponseCode() != 200) {
                System.out.println("Connection failed");
                System.exit(1);
            }
        } catch (IOException e) {
            System.out.println("Connection failed");
            System.exit(1);
        }

        String response = "";
        try {
            Scanner sc = new Scanner(connection.getInputStream());
            while (sc.hasNextLine()) {
                response += sc.nextLine() + "\n";
            }
            sc.close();
        } catch (IOException e) {
            System.out.println("Failed to create InputStream");
            System.exit(1);
        }

        JSONArray entry = new JSONArray(response);

        return entry.getJSONObject(0).getInt("idA" + table.substring(1));
    }

    /**
     * Get all new airlines from file.
     *
     * @param path Path to file.
     * @return JSONArray with all new airlines.
     * @throws FileNotFoundException If file is not found.
     */
    public static JSONArray getNewAirlines(String path) throws FileNotFoundException {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> tags = new ArrayList<>();
        Scanner sc = new Scanner(new File(path));
        String line;
        String[] lineSplit;

        while (sc.hasNextLine()) {
            line = sc.nextLine().trim();
            lineSplit = line.split("\\^");
            if (!tags.contains(lineSplit[0].substring(0, 2))) {
                tags.add(lineSplit[0].substring(0, 2));
                names.add(lineSplit[3]);
            }
        }

        JSONArray name = new JSONArray(names);
        JSONArray tag = new JSONArray(tags);

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
