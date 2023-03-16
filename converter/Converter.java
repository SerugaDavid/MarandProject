import com.mysql.cj.xdevapi.JsonArray;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Converter {
    public static void main(String[] args) {
        try {
            JSONArray airlines = getNewAirlines("./data_file.csv");
            // TODO: call API to add airlines to database

            JSONArray airports = getNewAirports("./data_file.csv");
            // TODO: call API to add airports to database

            JSONArray flights = getFlights("./data_file.csv");
            // TODO: call API to add flights to database
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            e.printStackTrace();
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
