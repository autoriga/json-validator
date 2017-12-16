import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;


public class Main {
    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create();
            server.bind(new InetSocketAddress(80), 0);
            server.createContext("/", new MyHandler());
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
