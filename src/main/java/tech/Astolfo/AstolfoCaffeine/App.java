package tech.Astolfo.AstolfoCaffeine;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    public static JDA jda;

    static {
        try {
            jda = JDABuilder
                    .createLight(System.getenv("TOKEN"))
                    .enableCache(CacheFlag.EMOTE)
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
                .setOwnerId(System.getenv("OWNER"))
                .setActivity(Activity.streaming("with pokimane", "https://www.twitch.tv/team_astolfo"))
                .setHelpWord("globglogabgalab")
                .addCommands(
                        // Ancap
                        new Market(),
                        new Buyorder(),
                        new Sellorder(),
                        new Portfolio(waiter),

                        // Business
                        new Create(waiter),
                        new Hire(),
                        new Info(waiter),
                        new Join(),
                        new Kick(),
                        new Leave(),
                        new SetImage(waiter),
                        new Transfer(),
                        new CreateStocks(waiter),
                        new GoPublic(waiter),

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
        Cache.client = client;

        jda.addEventListener(client);
        jda.addEventListener(waiter);

        new Database().clear_unused();
        new Database().clear_stocks();
    }
}
