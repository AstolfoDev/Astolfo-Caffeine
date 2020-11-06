package tech.Astolfo.AstolfoCaffeine.main.cmd.ancap;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.bson.Document;
import org.bson.conversions.Bson;
import tech.Astolfo.AstolfoCaffeine.main.db.CloudData;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;
import tech.Astolfo.AstolfoCaffeine.main.util.maths.Maths;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;

public class Portfolio extends Command {

    private final EventWaiter waiter;

    public Portfolio(EventWaiter waiter) {
        super.name = "portfolio";
        super.aliases = new String[]{"shares"};
        super.help = "view another user's shares";
        super.category = new Category("ancap");
        super.arguments = "<@user>";

        this.waiter = waiter;
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
    protected void execute(CommandEvent e) {
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

            if (user.isBot()) {
                msg.getChannel().sendMessage("**Beep boop!** soz ur robot overlords violate the NAP, begone!!!11!\n*ASTOolfo numbawh #1*").queue();
                return;
            }

            // Finalise the variable so it can be used in lambda expressions
            final User finalUser = user;

            // Retrieves the user's portfolio from the MongoDB "stocks" collection
            Document portfolio = new CloudData().get_data(id, CloudData.Database.Economy, CloudData.Collection.stocks);

            // The EmbedBuilder "eb" is initialised with basic information for the title/author
            EmbedBuilder eb = new Logging().embed()
                    .setAuthor(user.getAsTag() + " AstolfoEx Portfolio", "https://astolfo.tech", user.getAvatarUrl());


            // Goes through every entry in the Map and adds the appropriate information to the embed when needed
            securities.forEach(
                    (security_ticker, security_data) -> {
                        int shares_owned = portfolio.getInteger(security_data[0]);
                        if (shares_owned >= 1) {
                            eb.addField(security_data[1], shares_owned + " shares", true);
                        }
                    }
            );

            // Fallback message if the user does not own any securities
            if (eb.getFields().size() < 1) {
                eb.setDescription(user.getName() + " does not currently hold any shares.");
            } else if (user.equals(e.getAuthor())) {
                eb.setDescription("To transfer your shares react with \uD83E\uDD1D");
            }

            // Constructs the EmbedBuilder into a sendable MessageEmbed
            MessageEmbed embed = eb.build();
            // Sends the final embed to the user
            msg.getChannel().sendMessage(embed).queue(
                    message -> {
                        if (eb.getFields().size() >= 1 && finalUser.equals(e.getAuthor())) {
                            message.addReaction("\uD83E\uDD1D").queue();
                            transfer_waiter(e, message);
                        }
                    }
            );

        } catch (NumberFormatException err) {
            // Why is this needed? I have no clue. I should probably remove this try catch...
            msg.getChannel().sendMessage(new Logging().error("OooooOoh no! u gave me and invalid numbwer ;(\n*(Hint: make sure it's a whole number!)*")).queue();
        }
    }

    private void transfer_waiter(CommandEvent e, Message msg) {
        waiter.waitForEvent(
                GuildMessageReactionAddEvent.class,
                check -> e.getAuthor().getIdLong() == check.getUserIdLong() && msg.getIdLong() == check.getMessageIdLong(),
                action -> {
                    action.getReaction().removeReaction(action.getUser()).queue();
                    if (action.getReactionEmote().getName().equals("\uD83E\uDD1D")) {
                        transfer_start(e);
                    } else {
                        transfer_waiter(e, msg);
                    }
                }
        );
    }

    private void transfer_start(CommandEvent e) {
        Message message = e.getChannel().sendMessage("**Okayyyz!** Which 1z of ur companies do i wanna transfer st0ckz frommm hmmz?").complete();

        waiter.waitForEvent(
                GuildMessageReceivedEvent.class,
                check -> e.getAuthor().getId().equals(check.getAuthor().getId()) && e.getChannel().getId().equals(check.getChannel().getId()),
                action -> {

                    Message msg = action.getMessage();
                    String companyName = msg.getContentRaw().toLowerCase();

                    MongoCollection<Document> companyCol = new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.company);
                    Bson filter = eq("name", Pattern.compile(companyName, Pattern.CASE_INSENSITIVE));

                    Document companyDoc = companyCol.find(filter).first();
                    Document userShares = new CloudData().get_data(e.getAuthor().getIdLong(), CloudData.Database.Economy, CloudData.Collection.stocks);

                    if (companyDoc == null) {
                        message.delete().queue();
                        e.reply("**Soz!** dat company dont exist, fwend");
                        return;
                    }

                    if (!companyDoc.containsKey("ticker")) {
                        e.reply(String.format("**Ughhh** `%s` doesntttt trade sharezzzz ;P", companyDoc.getString("name")));
                        return;
                    }

                    String ticker = companyDoc.getString("ticker");

                    if (!userShares.containsKey(ticker)) {
                        e.reply(String.format("**Woahhhhh der** u no haz any shares in `%s` O_o", companyDoc.getString("name")));
                        return;
                    } else if (userShares.getInteger(ticker) <= 0) {
                        e.reply(String.format("**Woahhhhh der** u no haz any shares in `%s` O_o", companyDoc.getString("name")));
                        return;
                    }

                    message.delete().queue();
                    transfer_amount(e, userShares, companyDoc, ticker);
                }
        );
    }

    private void transfer_amount(CommandEvent e, Document userShares, Document companyDoc, String ticker) {
        Message message = e.getChannel().sendMessage(String.format("**Huh!** datz cool, how many `%s` shares u wantz?", companyDoc.getString("name"))).complete();

        waiter.waitForEvent(
                GuildMessageReceivedEvent.class,
                check -> e.getAuthor().getId().equals(check.getAuthor().getId()) && e.getChannel().getId().equals(check.getChannel().getId()),
                action -> {
                    Message msg = e.getMessage();
                    String msg_content = msg.getContentRaw();

                    if (!Maths.Validation.isNumeric(msg_content)) {
                        e.reply("**Right!** thas no numbawh!! numbers look like dis: *1, 2,3 ,4 5, 69 420*");
                        return;
                    }

                    int trade_amount = Integer.parseInt(msg_content);
                    int max_amount = userShares.getInteger(ticker);

                    if (trade_amount <= 0 || trade_amount > max_amount) {
                        e.reply(String.format("**heyyY heYYY!** cmon maaaaaate watttt ya playin at, u know u donz haz `%s` sharesSszzz", msg_content));
                        return;
                    }
                }
        );
    }
}