package tech.Astolfo.AstolfoCaffeine.main.web;
import com.google.gson.*;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStreamReader;
import java.io.InputStream;

public class webAPI {
    public double get_price(String ticker) {
        double price = 0.00;
        try {
            String sURL = "https://cloud.iexapis.com/stable/stock/"+ticker+"/quote/latestPrice?token="+System.getenv("STOCKAPI");

            URL url = new URL(sURL);
            URLConnection request;

            request = url.openConnection();
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement priceElement = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            price = priceElement.getAsDouble();
            return price;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return price;
    }
}