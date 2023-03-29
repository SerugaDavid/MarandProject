import org.json.JSONArray;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

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
