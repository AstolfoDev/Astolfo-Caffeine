package tech.Astolfo.AstolfoCaffeine;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.bson.Document;
import tech.Astolfo.AstolfoCaffeine.main.cmd.ancap.Buyorder;
import tech.Astolfo.AstolfoCaffeine.main.cmd.ancap.Market;
import tech.Astolfo.AstolfoCaffeine.main.cmd.ancap.Portfolio;
import tech.Astolfo.AstolfoCaffeine.main.cmd.ancap.Sellorder;
import tech.Astolfo.AstolfoCaffeine.main.cmd.business.*;
import tech.Astolfo.AstolfoCaffeine.main.cmd.economy.Balance;
import tech.Astolfo.AstolfoCaffeine.main.cmd.economy.Pay;
import tech.Astolfo.AstolfoCaffeine.main.cmd.economy.Shop;
import tech.Astolfo.AstolfoCaffeine.main.cmd.economy.Work;
import tech.Astolfo.AstolfoCaffeine.main.cmd.gambling.Casino;
import tech.Astolfo.AstolfoCaffeine.main.cmd.gambling.Coinflip;
import tech.Astolfo.AstolfoCaffeine.main.cmd.info.*;
import tech.Astolfo.AstolfoCaffeine.main.db.Database;
import tech.Astolfo.AstolfoCaffeine.main.event.Listener;
import tech.Astolfo.AstolfoCaffeine.main.util.caching.Cache;

import javax.security.auth.login.LoginException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    @Deprecated
    public static ConnectionString conStr = new ConnectionString(System.getenv("CON_URL"));
    @Deprecated
    public static MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(conStr)
            .retryWrites(true)
            .build();
    @Deprecated
    public static MongoClient mongoClient = MongoClients.create(settings);
    public static MongoDatabase db = mongoClient.getDatabase("Economy");
    @Deprecated
    public static MongoCollection<Document> col = db.getCollection("wallets");
    @Deprecated
    public static MongoCollection<Document> stocks = db.getCollection("stocks");
    @Deprecated
    public static MongoCollection<Document> company = db.getCollection("company");


    @Deprecated
    public static HashMap<Long, Long> cooldown = new HashMap<>();

    @Deprecated
    public static double round(double value, int places) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Deprecated
    public static String avatarURL;

    @Deprecated
    public static EmbedBuilder embed() {
        return new EmbedBuilder()
                .setFooter(System.getenv("VERSION_ID"), App.avatarURL)
                .setColor(0xde1073);
    }

    public static JDA jda;

    static {
        try {
            jda = JDABuilder
                    .createLight(System.getenv("TOKEN"))
                    .setActivity(Activity.watching("pokimane"))
                    .addEventListeners(new Listener())
                    .build();

            Cache.jda = jda;

        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);

        EventWaiter waiter = new EventWaiter();

        CommandClientBuilder builder = new CommandClientBuilder()
                .setPrefix(System.getenv("PREFIX"))
                .setOwnerId(System.getenv("OWNER"))
                .setActivity(Activity.streaming("with pokimane", "https://www.twitch.tv/team_astolfo"))
                .setHelpWord("globglogabgalab")
                .addCommands(
                        // Ancap
                        new Market(),
                        new Buyorder(),
                        new Sellorder(),
                        new Portfolio(),

                        // Business
                        new Create(waiter),
                        new Hire(),
                        new Info(waiter),
                        new Join(),
                        new Kick(),
                        new Leave(),
                        new SetImage(waiter),
                        new Transfer(),

                        // Economy
                        new Balance(),
                        new Pay(),
                        new Shop(waiter),
                        new Work(waiter),

                        // Gambling
                        new Casino(),
                        new Coinflip(),

                        // Info
                        new Help(waiter),
                        new Invite(),
                        new Leaderboard(waiter),
                        new Profile(),
                        new Stats()
                );

        CommandClient client = builder.build();

        jda.addEventListener(client);
        jda.addEventListener(waiter);

        new Database().clear_unused();
        new Database().clear_stocks();
    }
}
