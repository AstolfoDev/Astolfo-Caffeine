package tech.Astolfo.AstolfoCaffeine.main.web;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class keepOnline { 

  public static void innit() throws IOException {
      HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
      HttpContext context = server.createContext("/");
      context.setHandler(keepOnline::handleRequest);
      server.start();
  }
	
	private static void handleRequest(HttpExchange exchange) throws IOException {
      String response = "You were expecting to see your weeb BS here, but it was I, Dio!";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
  }
	
}