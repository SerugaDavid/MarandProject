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
    }

    /**
     * Test connection to database.
     * @return true if connection is successful, false otherwise.
     */
    public boolean testConnection() {
        try {
            Connection connection = DriverManager.getConnection(this.connectionUrl, this.userPass, this.userPass);
            connection.close();
            return true;
        } catch (SQLException e) {
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
        while (keys.hasNext()) {
            String key = keys.next();
            update += "'" + entry.get(key) + "', ";
        }
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
        update = update.substring(0, update.length() - 2);
        update += ";";

        return update;
    }

    /**
     * Get all flights from database.
     * @return JSONObject with all flights.
     */
    public JSONObject getFlights() {
        String query = "SELECT * FROM Flights;";
        try {
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
    private JSONObject generateJsonFromResult(ResultSet resultSet) {
        try {
            String jsonString = "{";
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                jsonString += "{";
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String value = resultSet.getString(columnName);
                    jsonString += "\"" + columnName + "\": \"" + value + "\", ";
                }
                jsonString = jsonString.substring(0, jsonString.length() - 2);
                jsonString += "}, ";
            }
            jsonString = jsonString.substring(0, jsonString.length() - 2);
            jsonString += "}";
            return new JSONObject(jsonString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getEntry(String id) {
        return null;
    }
}