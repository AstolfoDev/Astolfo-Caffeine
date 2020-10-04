package tech.Astolfo.AstolfoCaffeine.main.cmd.fun;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class RandomAstolfo extends Command {
    public RandomAstolfo() {
        super.name = "astolfo";
        super.aliases = new String[]{"astolfo"};
        super.help = "sell shares on the market";
        super.category = new Category("ancap");
        super.arguments = "<stock> <amt>";
    }

    @Override
    protected void execute(CommandEvent e) {

        String inline = "";
        try {
            URL url = new URL("https://astolfo.rocks/api/v1/images/random");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int response = conn.getResponseCode();
            if(response != 200) {
                throw new RuntimeException("HttpURLConnection " + response);
            }else {
                Scanner sc = new Scanner(url.openStream());

                while(sc.hasNext()) {
                    inline += sc.nextLine();
                }
                sc.close();

                String image_link = "";
                while(true){
                    JsonElement jelement = new JsonParser().parse(inline);
                    JsonObject jobject = jelement.getAsJsonObject();
                    image_link = jobject.get("url").getAsString();
                    String raiting = jobject.get("url").getAsString();

                    if(raiting == "Safe") {
                        break;
                    }else {
                        conn.disconnect();
                        conn.setRequestMethod("GET");
                        conn.connect();
                    }
                }
                e.reply(image_link);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
