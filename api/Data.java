import com.mysql.cj.exceptions.WrongArgumentException;

import java.sql.*;
import java.util.Iterator;
import java.util.Map;
import org.json.*;

public class Data {
    private String connectionUrl;
    private String userPass;

    public Data() {
        this.connectionUrl = "jdbc:mysql://localhost:3306/FlyByNight";
        this.userPass = "root";

        // TODO: add JSONArray to dependency list for jetty
        // TODO: add JSONObject to dependency list for jetty
        // TODO: give up?
    }

    /**
     * Test connection to database.
     * @return true if connection is successful, false otherwise.
     */
    public boolean testConnection() {
        try {
            Connection connection = DriverManager.getConnection(this.connectionUrl, this.userPass, this.userPass);
            connection.close();
            System.out.println("Connection successful!");
            return true;
        } catch (SQLException e) {
            System.out.println("Connection failed! in Data.java");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Method for inserting data into database.
     * Requires an update string.
     * @param update SQL update string.
     */
    private void insertInto(String update) {
        try {
            System.out.println(update);
            Connection connection = DriverManager.getConnection(this.connectionUrl, this.userPass, this.userPass);
            Statement statement = connection.createStatement();
            statement.executeUpdate(update);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add flight entry to the database.
     * @param entry JSONObject with flight data.
     */
    public void addFlight(JSONObject entry) {
        String update = generateUpdateString(entry);
        insertInto(update);
    }

    /**
     * Adds multiple flight entries to the database.
     * @param flights JSONArray with data from multiple flights.
     */
    public void addFlights(JSONArray flights) {
        for (int i = 0; i < flights.length(); i++) {
            JSONObject entry = flights.getJSONObject(i);
            addFlight(entry);
        }
    }

    /**
     * Generate SQL update string from json entry. Only for one entry.
     * @param entry JSONObject with flight data.
     * @return SQL update string.
     */
    private String generateUpdateString(JSONObject entry) {
        Iterator<String> keys = entry.keys();
        String update = "INSERT INTO Flights (";
        while (keys.hasNext()) {
            String key = keys.next();
            update += key + ", ";
        }
        update = update.substring(0, update.length() - 2);
        update += ") VALUES (";
        keys = entry.keys();
        int count = 0;
        while (keys.hasNext()) {
            String key = keys.next();
            update += "'" + entry.get(key) + "', ";
            count++;
        }
        if (count != 0)
            update = update.substring(0, update.length() - 2);
        update += ");";
        return update;
    }

    /**
     * Add airports to the database.
     * @param airports JSONArray with airport abbreviations.
     */
    public void addAirports(JSONArray airports) {
        String update = generateAirportString(airports);
        insertInto(update);
    }

    /**
     * Generate SQL update string from json array with airports.
     * @param airports JSONArray with airport abbreviations.
     * @return SQL update string.
     */
    private String generateAirportString(JSONArray airports) {
        String update = "INSERT INTO Airports (Name, Abbreviation) VALUES ";
        for (int i = 0; i < airports.length(); i++) {
            update += "('" + airports.getString(i) + "', '" + airports.getString(i) + "'), ";
        }
        if (airports.length() != 0)
            update = update.substring(0, update.length() - 2);
        update += ";";
        return update;
    }

    /**
     * Add airlines to the database.
     * @param airlines JSONArray with nested JSONArray airline names and tags.
     */
    public void addAirlines(JSONArray airlines) {
        String update = generateAirlinesString(airlines);
        insertInto(update);
    }

    /**
     * Generate SQL update string from json array with airlines.
     * @param airlines JSONArray with nested JSONArray airline names and tags.
     * @return SQL update string.
     */
    private String generateAirlinesString(JSONArray airlines) {
        JSONArray name = airlines.getJSONArray(0);
        JSONArray tag = airlines.getJSONArray(1);
        String update = "INSERT INTO Airlines (Name, Tag) VALUES ";

        for (int i = 0; i < name.length(); i++) {
            update += "('" + name.getString(i) + "', '" + tag.getString(i) + "'), ";
        }
        if (name.length() != 0)
            update = update.substring(0, update.length() - 2);
        update += ";";

        return update;
    }
    public JSONArray getFlights() {
        String query = "SELECT * FROM Flights;";
        return executeQuery(query);
    }

    public JSONArray getFlightsById(int id) {
        String query = "SELECT * FROM Flights Where idFlights = " + id + ";";
        return executeQuery(query);
    }

    public JSONArray getFlightsByOriginDestination(int origin, int destination) {
        String query = "SELECT * FROM Flights";
        if (origin != -1)
            query += " WHERE OriginAirportID = " + origin;
        if (destination != -1) {
            if (origin != -1)
                query += " AND";
            else
                query += " WHERE";
            query += " DestinationAirportID = " + destination;
        }
        query += ";";
        return executeQuery(query);
    }

    /**
     * TODO:
     * - getFlightsByFlightNumber
     * - getFlightsByAirline
     * - getFlightsByOrigin
     * - getFlightsByDestination
     * - ...
     */

    public JSONArray getAirlines() {
        String query = "SELECT * FROM Airlines;";
        return executeQuery(query);
    }

    public JSONArray getAirlinesById(int id) {
        String query = "SELECT * FROM Airlines WHERE idAirlines = " + id + ";";
        return executeQuery(query);
    }

    public JSONArray getAirlinesByName(String name) {
        String query = "SELECT * FROM Airlines WHERE Name = '" + name + "';";
        return executeQuery(query);
    }

    public JSONArray getAirports() {
        String query = "SELECT * FROM Airports;";
        return executeQuery(query);
    }

    public JSONArray getAirportsById(int id) {
        String query = "SELECT * FROM Airports WHERE idAirports = " + id + ";";
        return executeQuery(query);
    }

    public JSONArray getAirportsByAbbreviation(String abbreviation) {
        String query = "SELECT * FROM Airports WHERE Abbreviation = '" + abbreviation + "';";
        return executeQuery(query);
    }

    /**
     * Makes connection to the database, executes a give query and returns the result
     * @param query MySql query
     * @return JSONArray created from result
     */
    private JSONArray executeQuery(String query) {
        try {
            System.out.println(query);
            Connection connection = DriverManager.getConnection(this.connectionUrl, this.userPass, this.userPass);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            return generateJsonFromResult(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Generate json from the result set.
     * @param resultSet Result set from the database.
     * @return JSONObject from the result set.
     */
    private JSONArray generateJsonFromResult(ResultSet resultSet) {
        try {
            String jsonString = "[";
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                jsonString += "{";
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    switch (columnName) {
                        case "idFlights":
                        case "idAirlines":
                        case "idAirports":
                        case "OriginAirportID":
                        case "DestinationAirportID":
                        case "Airlines_idAirlines":
                        case "AvailableSeats":
                            int intValue = resultSet.getInt(columnName);
                            jsonString += "\"" + columnName + "\": " + intValue + ", ";
                            break;
                        case "FlightNumber":
                        case "Day":
                        case "Name":
                        case "Tag":
                        case "Abbreviation":
                            String stringValue = resultSet.getString(columnName);
                            jsonString += "\"" + columnName + "\": \"" + stringValue + "\", ";
                            break;
                        case "Price":
                            double doubleValue = resultSet.getDouble(columnName);
                            jsonString += "\"" + columnName + "\": " + doubleValue + ", ";
                            break;
                        case "Time":
                        case "Duration":
                            Time timeValue = resultSet.getTime(columnName);
                            jsonString += "\"" + columnName + "\": \"" + timeValue.toString() + "\", ";
                            break;
                        default:
                            break;
                    }
                }
                jsonString = jsonString.substring(0, jsonString.length() - 2);
                jsonString += "}, ";
            }
            if (jsonString.length() > 2)
                jsonString = jsonString.substring(0, jsonString.length() - 2);
            jsonString += "]";
            System.out.println(jsonString);
            return new JSONArray(jsonString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("[]");
        return new JSONArray("[]");
    }

    public boolean bookFlight(JSONArray flight) {
        int flightId = flight.getInt(0);
        String query = "SELECT AvailableSeats FROM flights WHERE idFlights = " + flightId + ";";
        JSONArray result = executeQuery(query);
        if (result.length() == 0)
            return false;
        int availableSeats = result.getJSONObject(0).getInt("AvailableSeats");
        if (availableSeats == 0)
            return false;
        query = "UPDATE Flights SET AvailableSeats = AvailableSeats - 1 WHERE idFlights = " + flightId + ";";
        executeQuery(query);
        return true;
    }
}
