import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;
import java.util.Vector;

public class FlightsApp {
    private JPanel MainPanel;
    private JComboBox origin;
    private JComboBox destination;
    private JTable flightsTable;
    private JButton findFlightsButton;
    private JLabel Heading;
    private JLabel originLable;
    private JLabel destinationLabel;
    private String url;

    public FlightsApp() {
        findFlightsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String origin = (String) FlightsApp.this.origin.getSelectedItem();
                String destination = (String) FlightsApp.this.destination.getSelectedItem();
                JSONArray airports = getAirportsArray();
                JSONArray airlines = getAirlinesArray();
                JSONArray flights = getFlights(origin, destination, airports);
                updateTable(flights, airports, airlines);
            }
        });

        /*
         * Fill data in both combo boxes
         * Api call to get all airports
         * Convert to String array
         * Add to combo boxes
         */
        this.url = "http://localhost:8080/api/flights";
        String[] airports = getAirports();
        for (String airport : airports) {
            origin.addItem(airport);
            destination.addItem(airport);
        }


    }

    /**
     * Main method.
     * @param args
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("FlightsApp");
        frame.setContentPane(new FlightsApp().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Returns all airport names from API and adds "any" to the beginning.
     * @return String array with all airport names.
     */
    private String[] getAirports() {
        JSONArray airports = getAirportsArray();
        String[] airportNames = new String[airports.length() + 1];
        airportNames[0] = "Kjerkoli";
        for (int i = 1; i < airports.length(); i++) {
            airportNames[i] = airports.getJSONObject(i - 1).getString("Name");
        }
        return airportNames;
    }

    /**
     * Calls API to get all airports.
     * @return JSONArray with all airports.
     */
    private JSONArray getAirportsArray() {
        String url = this.url + "/airports";

        return connect(url);
    }

    private JSONArray getAirlinesArray() {
        String url = this.url + "/airlines";

        return connect(url);
    }

    private JSONArray getFlights(String origin, String destination, JSONArray airports) {
        int o = 0;
        int d = 0;

        for (int i = 0; i < airports.length(); i++) {
            if (airports.getJSONObject(i).getString("Name").equals(origin)) {
                o = airports.getJSONObject(i).getInt("idAirports");
            }
            if (airports.getJSONObject(i).getString("Name").equals(destination)) {
                d = airports.getJSONObject(i).getInt("idAirports");
            }
        }

        String url = this.url + "/flights/origin-destination/" + o + "/" + d;
        return connect(url);
    }

    private void updateTable(JSONArray flights, JSONArray airports, JSONArray airlines) {
        if (flights.length() == 0) {
            System.out.println("No flights found");
            return;
        }

        Vector<String> columns = getColumns(flights);
        Vector<Vector<String>> data = getData(flights, airports, airlines);

        this.flightsTable.setModel(new DefaultTableModel(data, columns));
    }
    
    private Vector<Vector<String>> getData(JSONArray flights, JSONArray airports, JSONArray airlines) {
        Vector<Vector<String>> data = new Vector<>();
        for (int i = 0; i < flights.length(); i++) {
            Vector<String> row = new Vector<>();
            JSONObject flight = flights.getJSONObject(i);

            row.add(flight.getString("FlightNumber"));
            row.add(getAirportName(flight.getInt("OriginAirportID"), airports));
            row.add(getAirportName(flight.getInt("DestinationAirportID"), airports));
            row.add(getAirlineName(flight.getInt("Airlines_idAirlines"), airlines));
            row.add(flight.getString("Price"));
            row.add(flight.getString("Day"));
            row.add(flight.getString("Time"));
            row.add(flight.getString("Duration"));
            row.add(flight.getString("AvailableSeats"));

            data.add(row);
        }
        return data;
    }

    private String getAirportName(int id, JSONArray airports) {
        for (int i = 0; i < airports.length(); i++) {
            if (airports.getJSONObject(i).getInt("idAirports") == id) {
                return airports.getJSONObject(i).getString("Name");
            }
        }
        return "";
    }

    private String getAirlineName(int id, JSONArray airlines) {
        for (int i = 0; i < airlines.length(); i++) {
            if (airlines.getJSONObject(i).getInt("idAirlines") == id) {
                return airlines.getJSONObject(i).getString("Name");
            }
        }
        return "";
    }

    private Vector<String> getColumns(JSONArray flights) {
        Vector<String> columns = new Vector<>();
        columns.add("Številka leta");
        columns.add("Odhodno letališče");
        columns.add("Prihodno letališče");
        columns.add("Letalska družba");
        columns.add("Cena");
        columns.add("Dan");
        columns.add("Čas odhoda");
        columns.add("Dolžina leta");
        columns.add("Prosti sedeži");
        return columns;
    }

    private JSONArray connect(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
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
        return entry;
    }
}
