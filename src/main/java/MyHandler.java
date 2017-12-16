import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class My_Handler implements HttpHandler {
    private int id = -1;
    public void handle(HttpExchange exchange) throws IOException {
        id = id + 1;
        BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        String readString = br.readLine();
        String string = "";
        while(readString != null) {
            string = string.concat(readString);
            readString = br.readLine();
        }

        GsonBuilder gb = new GsonBuilder();
        gb.setPrettyPrinting().serializeNulls();
        Gson gson = gb.create();
        String str = null;
        try {
            Object object = gson.fromJson(string, Object.class);
            str = gson.toJson(object);
        } catch (JsonSyntaxException ex) {
            JsonObject jsonObject = new JsonObject();
            String errorDescriptionPart = ex.getMessage().split(": ")[1];
            String errorMessage = errorDescriptionPart.split(" at ")[0];
            String errorOccurrence = errorDescriptionPart.split(" at ")[1];
            int errorCode = ex.hashCode();
            jsonObject.addProperty("connection-id", id);
            jsonObject.addProperty("errorMessage", errorMessage);
            jsonObject.addProperty("errorOccurrence", errorOccurrence);
            jsonObject.addProperty("errorCode", errorCode);
            str = gson.toJson(jsonObject);
        }
        System.out.println(str);
        exchange.sendResponseHeaders(200, str.length());
        OutputStream os = exchange.getResponseBody();
        os.write(str.getBytes());
        os.close();
    }
}
