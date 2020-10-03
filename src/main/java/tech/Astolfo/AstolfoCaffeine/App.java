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
import tech.Astolfo.AstolfoCaffeine.main.util.minecraft.Toolbox;

import javax.security.auth.login.LoginException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    public static ConnectionString conStr = new ConnectionString(System.getenv("CON_URL"));
    public static MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(conStr)
            .retryWrites(true)
            .build();
    public static MongoClient mongoClient = MongoClients.create(settings);
    public static MongoDatabase db = mongoClient.getDatabase("Economy");
    public static MongoCollection<Document> col = db.getCollection("wallets");
    public static MongoCollection<Document> stocks = db.getCollection("stocks");
    public static MongoCollection<Document> company = db.getCollection("company");

    public static HashMap<Long, Long> cooldown = new HashMap<>();

    public static double round(double value, int places) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    public static String avatarURL;

    public static EmbedBuilder embed() {
        return new EmbedBuilder()
                .setFooter(System.getenv("VERSION_ID"), App.avatarURL)
                .setColor(0xde1073);
    }

    public static JDA jda;

    static {
        try {
            jda = new JDABuilder()
                        .setToken(System.getenv("TOKEN"))
                        .setActivity(Activity.watching("pokimane"))
                        .addEventListeners(new Listener())
                        .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);

        EventWaiter workWaiter = new EventWaiter();
        EventWaiter createWaiter = new EventWaiter();
        EventWaiter infoWaiter = new EventWaiter();
        EventWaiter helpWaiter = new EventWaiter();
        EventWaiter setImageWaiter = new EventWaiter();
        EventWaiter leaderboardWaiter = new EventWaiter();

        CommandClientBuilder builder = new CommandClientBuilder()
                .setPrefix(System.getenv("PREFIX"))
                .setOwnerId(System.getenv("OWNER"))
                .setActivity(Activity.streaming("with pokimane", "https://www.twitch.tv/team_astolfo"))
                .setHelpWord("globglogabgalab")
                .addCommand(new Balance())
                .addCommand(new Pay())
                .addCommand(new Work(workWaiter))
                .addCommand(new Market())
                .addCommand(new Buyorder())
                .addCommand(new Sellorder())
                .addCommand(new Portfolio())
                .addCommand(new Create(createWaiter))
                .addCommand(new Hire())
                .addCommand(new Kick())
                .addCommand(new Join())
                .addCommand(new Info(infoWaiter))
                .addCommand(new Profile())
                .addCommand(new SetImage(setImageWaiter))
                .addCommand(new Leave())
                .addCommand(new Casino())
                .addCommand(new Coinflip())
                .addCommand(new Help(helpWaiter))
                .addCommand(new Invite())
                .addCommand(new Leaderboard(leaderboardWaiter))
                .addCommand(new Stats())
                .addCommand(new Transfer())
                .addCommand(new Shop(workWaiter));;

        CommandClient client = builder.build();
        
        jda.addEventListener(client);
        jda.addEventListener(workWaiter);
        jda.addEventListener(createWaiter);
        jda.addEventListener(infoWaiter);
        jda.addEventListener(helpWaiter);
        jda.addEventListener(setImageWaiter);
        jda.addEventListener(leaderboardWaiter);

        new Database().clear_unused();
        new Database().clear_stocks();

        System.out.println("hiya");
    }
}
