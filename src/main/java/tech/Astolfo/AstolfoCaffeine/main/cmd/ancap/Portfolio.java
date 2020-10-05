package tech.Astolfo.AstolfoCaffeine.main.cmd.ancap;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import tech.Astolfo.AstolfoCaffeine.main.db.CloudData;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;

import java.util.HashMap;
import java.util.Map;

public class Portfolio extends Command {

    public Portfolio() {
        super.name = "portfolio";
        super.aliases = new String[]{"shares"};
        super.help = "view another user's shares";
        super.category = new Category("ancap");
        super.arguments = "<@user>";
    }

    // Map containing all the information for each tradable stock
    private final Map<Ticker, String[]> securities = new HashMap<Ticker, String[]>() {{
        put(Ticker.astf, new String[]{"astf", "Team Astolfo **(ASTF)**"});
        put(Ticker.gudk, new String[]{"gudk", "Gudako, Corp. **(GUDK)**"});
        put(Ticker.vimx, new String[]{"vimx", "KAY&VIM Index **(^VIM)**"});
        put(Ticker.weeb, new String[]{"weeb", "Ishtar Motors **(WEEB)**"});
        put(Ticker.wolf, new String[]{"wolf", "Meliodaf, Inc. **(WOLF)**"});
        put(Ticker.emo, new String[]{"emo", "Emortal, Inc. **(EMO)**"});
    }};

    // Enum containing values for each tradable stock's ticker (identifier)
    enum Ticker {
        astf,
        gudk,
        vimx,
        weeb,
        wolf,
        emo
    }

    @Override
    protected void execute (CommandEvent e) {
        // Get Message object from CommandEvent and intiialise arguments as a string array
        Message msg = e.getMessage();
        String[] args = e.getArgs().split("\\s+");

        try {
            // Intialise user's id and user object as variables
            long id = msg.getAuthor().getIdLong();
            User user = msg.getAuthor();

            // TODO: Cleanup messy arguments handler
            if (args.length == 1 && args[0].startsWith("<")) {
                long temp = Long.parseLong(args[0].replace("<", "").replace("@", "").replace("!", "").replace(">", ""));
                if (msg.getMentionedUsers().get(0).getIdLong() == temp) {
                    id = temp;
                    user = msg.getMentionedUsers().get(0);
                }
            } else if (args.length > 1) {
                msg.getChannel().sendMessage("Too many arguments! Defaulting to command issuer...").queue();
            }

            // Retrieves the user's portfolio from the MongoDB "stocks" collection
            Document portfolio = new CloudData().get_data(id, CloudData.Collection.stocks);

            // The EmbedBuilder "eb" is initialised with basic information for the title/author
            EmbedBuilder eb = new Logging().embed()
                    .setAuthor(user.getAsTag() + " AstolfoEx Portfolio", "https://astolfo.tech", user.getAvatarUrl());


            // Goes through every entry in the Map and adds the appropriate information to the embed when needed
            securities.forEach(
                    (security_ticker, security_data) -> {
                        int shares_owned = portfolio.getInteger(security_data[0]);
                        if (portfolio.getInteger(shares_owned) >= 1) {
                            eb.addField(security_data[1], shares_owned + " shares", true);
                        }
                    }
            );

            // Fallback message if the user does not own any securities
            if (eb.getFields().size() < 1) {
                eb.setDescription(user.getName() + " does not currently hold any shares.");
            }

            // Constructs the EmbedBuilder into a sendable MessageEmbed
            MessageEmbed embed = eb.build();
            // Sends the final embed to the user
            msg.getChannel().sendMessage(embed).queue();

        } catch (NumberFormatException err) {
            // Why is this needed? I have no clue. I should probably remove this try catch...
            msg.getChannel().sendMessage(new Logging().error("OooooOoh no! u gave me and invalid numbwer ;(\n*(Hint: make sure it's a whole number!)*")).queue();
        }

    }
}