package main.java;

import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;

/**
 * Class to implement server and validate json files
 */
public class json_val {

    private static final int PORT = 80;    
    private static final int MISTAKE = 200;  

    /**
     * @param server is newly created server in 'main' function, we use this param to bind server and create context
     * @throws IOException in case of something wrong with input/output operations
     */

    public json_val(HttpServer server) throws IOException {

        // We create GsonBuilder and configure it to output Json that fits in a page for pretty printing
        // Then we bind port with the server
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        server.bind(new InetSocketAddress(PORT), 0);

        // Start
        server.createContext("/", httpExchange -> {

            int countId = 0; // Counter of request-id

            /* 
		BufferedReader for text reading from a character-input stream, read until we get EOF
*/

            BufferedReader br = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));
            String str = br.readLine();

            StringBuilder stringBuilder = new StringBuilder();
            while (str != null) {
                
                stringBuilder.append(str);
                str = br.readLine();
            }

            // Show request
            String request = stringBuilder.toString();
            System.out.println("Request: " + request);

            String response = null;

            //Convert json to string, waiting for possible exceptions
            try {
                // Json to string
                Object object = gson.fromJson(request, Object.class);
                // String to Json
                response = gson.toJson(object);
                // In case of an exception do:
            } catch (JsonSyntaxException exception) {

                // Create json foundation for response
                JsonObject error = new JsonObject();

                // Extract part of the error before ": "
                String errorDescription = exception.getMessage().split(": ")[1];

                // Message
                String errorMessage = errorDescription.split(" at ")[0];
                // Place
                String errorPlace = errorDescription.split(" at ")[1];

                // Error's hash
                int errorCode = exception.hashCode();

                // Forming convenient view for the response
                error.addProperty("errorCode", errorCode);
                error.addProperty("errorMessage", errorMessage);
                error.addProperty("errorPlace", errorPlace);
                error.addProperty("resource", request);
                error.addProperty("request-id", countId);
                response = gson.toJson(error);
            }
            countId ++ ;
            System.out.println("Response: " + response);

            // Response and closing
            httpExchange.sendResponseHeaders(MISTAKE, response.length());
            httpExchange.getResponseBody().write(response.getBytes());
            httpExchange.close();
        });
    }

    /**
     * Main function for starting server and handle recieved json files
     * @throws IOException in case of smth wrong with input/output operations
     */
    public static void main(String[] args) throws IOException {

        // we created a server

        final HttpServer server = HttpServer.create();
        // create object of class
        json_val json = new json_val(server);

        // we launched the server

        json.start(server);
    }
}

    private static void start(HttpServer server) {server.start();}
    private static void stop (HttpServer server) {server.stop(0);}
