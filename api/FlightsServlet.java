import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.*;

@WebServlet("/flights/*")
public class FlightsServlet extends HttpServlet {

    private Data data;

    public FlightsServlet() {
        super();
        this.data = new Data();
    }

    /**
     * API get call.
     * Classifies what kind of GET request this is by url.
     * Gets the data from the url and sends a correct json.
     * If there was a problem or there is no data it sends empty json.
     *
     * @param request request
     * @param response response
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String requestUrl = request.getRequestURI();
        String url = fixUrl(requestUrl);

        response.setContentType("application/json");
        response.getOutputStream().println(url);

        String substring = getSubstringUrl(url);

        // On what table is this call
        switch (substring) {
            case "airports":
                // get airports
                url = url.substring("airports".length());
                url = fixUrl(url);
                substring = getSubstringUrl(url);

                // what data should we get
                switch (substring) {
                    case "abbreviation":
                        url = url.substring("id".length());
                        url = fixUrl(url);
                        if (url.length() == 0) {
                            response.getOutputStream().println("[]");
                            break;
                        }
                        if (this.data.testConnection()) {
                            response.getOutputStream().println(this.data.getAirportsByAbbreviation(url).toString());
                        } else {
                            response.getOutputStream().println("[]");
                        }
                        break;
                    case "id":
                        url = url.substring("id".length());
                        url = fixUrl(url);
                        int id = 0;
                        try {
                            id = Integer.parseInt(url);
                        } catch (NumberFormatException e) {
                            response.getOutputStream().println("[]");
                            break;
                        }
                        if (this.data.testConnection()) {
                            response.getOutputStream().println(this.data.getAirportsById(id).toString());
                        } else {
                            response.getOutputStream().println("[]");
                        }
                        break;
                    case "":
                        if (this.data.testConnection()) {
                            response.getOutputStream().println(this.data.getAirports().toString());
                        } else {
                            response.getOutputStream().println("[]");
                        }
                        break;
                    default:
                        response.getOutputStream().println("[]");
                        break;
                }
                break;
            case "airlines":
                // get airlines
                url = url.substring("airlines".length());
                substring = getSubstringUrl(url);

                // what data should we get
                switch (substring) {
                    case "name":
                        url = url.substring("name".length());
                        url = fixUrl(url);
                        if (url.length() == 0) {
                            response.getOutputStream().println("[]");
                            break;
                        }
                        if (this.data.testConnection()) {
                            response.getOutputStream().println(this.data.getAirlinesByName(url).toString());
                        } else {
                            response.getOutputStream().println("[]");
                        }
                       break;
                    case "id":
                        url = url.substring("id".length());
                        url = fixUrl(url);
                        int id = 0;
                        try {
                            id = Integer.parseInt(url);
                        } catch (NumberFormatException e) {
                            response.getOutputStream().println("[]");
                            break;
                        }
                        if (this.data.testConnection()) {
                            response.getOutputStream().println(this.data.getAirlinesById(id).toString());
                        } else {
                            response.getOutputStream().println("[]");
                        }
                        break;
                    case "":
                        if (this.data.testConnection()) {
                            response.getOutputStream().println(this.data.getAirlines().toString());
                        } else {
                            response.getOutputStream().println("[]");
                        }
                        break;
                    default:
                        response.getOutputStream().println("[]");
                        break;
                }
                break;
            case "flights":
                // TODO: get flights
                url = url.substring("flights".length());
                substring = getSubstringUrl(url);

                // what data should we get
                switch (substring) {
                    case "id":
                        url = url.substring("id".length());
                        url = fixUrl(url);
                        int id = 0;
                        try {
                            id = Integer.parseInt(url);
                        } catch (NumberFormatException e) {
                            response.getOutputStream().println("[]");
                            break;
                        }
                        if (this.data.testConnection()) {
                            response.getOutputStream().println(this.data.getFlightsById(id).toString());
                        } else {
                            response.getOutputStream().println("[]");
                        }
                        break;
                    case "":
                        if (this.data.testConnection()) {
                            response.getOutputStream().println(this.data.getFlights().toString());
                        } else {
                            response.getOutputStream().println("[]");
                        }
                }

                break;
            default:
                response.getOutputStream().println("Invalid url!");
                break;
        }
    }

    /**
     * Gets the first substring of url (url till the '/')
     * @param url Original url
     * @return First substring of url
     */
    private String getSubstringUrl(String url) {
        String substring;
        if (url.contains("/"))
            substring = url.substring(0, url.indexOf('/'));
        else
            substring = url;
        return substring;
    }

    /**
     * API post call. Adds received json to database. Only one entry.
     *
     * @param request  request
     * @param response response
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String requestUrl = request.getRequestURI();
        String url = fixUrl(requestUrl);

        if (!this.data.testConnection()) {
            System.out.println("Connection failed!");
            return;
        }
        switch (url) {
            case "airports":
                JSONArray jsonAirports = new JSONArray(getPostBody(request));
                this.data.addAirports(jsonAirports);
                break;
            case "airlines":
                JSONArray jsonAirlines = new JSONArray(getPostBody(request));
                this.data.addAirlines(jsonAirlines);
                break;
            case "":
                JSONArray jsonFlights = new JSONArray(getPostBody(request));
                this.data.addFlights(jsonFlights);
                break;
            default:
                response.getOutputStream().println("Invalid url!");
                break;
        }

    }

    /**
     * Reads post body from request and generates a json string.
     *
     * @param request request from post call
     * @return json string
     */
    private static String getPostBody(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Removes leading and trailing slashes from url.
     * @param requestUrl url from request
     * @return url without leading and trailing slashes
     */
    private String fixUrl(String requestUrl) {
        String url = requestUrl.substring("/api/flights".length());
        if (url.length() > 0 && url.charAt(0) == '/')
            url = url.substring(1);
        if (url.length() > 0 && url.charAt(url.length() - 1) == '/')
            url = url.substring(0, url.length() - 1);
        return url;
    }
}