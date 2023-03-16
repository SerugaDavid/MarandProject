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
     * API get call. Prints json of all flights to response output stream.
     *
     * @param request  request
     * @param response response
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String requestUrl = request.getRequestURI();
        String instruction = requestUrl.substring("/api/flights/".length());

        response.setContentType("application/json");
        response.getOutputStream().println(instruction);

        /*
        JSONObject json;
        if (this.data.testConnection())
            json = this.data.getFlights();
        else {
            System.out.println("Connection failed!");
            return;
        }
        if (json != null)
            response.getOutputStream().println(json.toString());
        else
            response.getOutputStream().println("{}");

         */
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
        String instruction = requestUrl.substring("/api/flights/".length());

        if (!this.data.testConnection()) {
            System.out.println("Connection failed!");
            return;
        }
        switch (instruction) {
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
}