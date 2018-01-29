package main.java;

import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;

/**
 * create class to implement server and validate json files
 */
public class json_val_class {
	
	 /**
     * Bind and start listening || finish work
     * @param start/close server, that we get as a parameter
     */
    private static void start(HttpServer server) {server.start();}
    private static void stop (HttpServer server) {server.stop(0);}
	
	/**
	*listen port and reply code
	*/
	
    private static final int PORT = 80;     
    private static final int MISTAKE = 200;    


    /**
     * Json-validation trying
     * @param server is newly created server in 'main' function, we use this param to bind server and create context
     * @throws IOException in case of something wrong with input/output operations
     */

    public json_val_class(HttpServer server) throws IOException {

        // We create GsonBuilder and configure it to output Json that fits in a page for pretty printing
        // Then we bind port with the server
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        server.bind(new InetSocketAddress(PORT), 0);

        // Start working
        server.createContext("/", httpExchange -> {

            int countId = 0; // count the id requests

            // BufferedReader for text reading from a character-input stream, read until we get EOF
            BufferedReader br = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));
            String str = br.readLine();
			String filename = httpExchange.getRequestURI().getPath();
			
            StringBuilder stringBuilder = new StringBuilder();
            while (str != null) {
                
                stringBuilder.append(str);
                str = br.readLine();
            }

            // View request
            String request = stringBuilder.toString();
            System.out.println("Request: " + request);

            String response = null;

            //Convert json to string, to see possible exceptions
            try {
                // we convert Json to string
                Object object = gson.fromJson(request, Object.class);
                // and we convert String to Json
                response = gson.toJson(object);
                // if there is an exception then execute::
            } catch (JsonSyntaxException exception) {

                // and now we create json foundation for response
                JsonObject error = new JsonObject();

                // then we extract the error part to ": "
                String errorDescription = exception.getMessage().split(": ")[1];

                // here is the message
                String errorMessage = errorDescription.split(" at ")[0];
                // but here is the place
                String errorPlace = errorDescription.split(" at ")[1];

                // Hash of an error
                int errorCode = exception.hashCode();

                // Create a normal response
                error.addProperty("errorCode", errorCode);
                error.addProperty("errorMessage", errorMessage);
                error.addProperty("errorPlace", errorPlace);
                error.addProperty("resource", filename);
                error.addProperty("request-id", countId);
                response = gson.toJson(error);
            }
            countId ++ ;
            System.out.println("Response: " + response);

            // now we send response and close
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

        // First create a server then create object of class and launch server
        final HttpServer server = HttpServer.create();
        json_val_class json = new json_val_class(server);
        json.start(server);
    }
}
