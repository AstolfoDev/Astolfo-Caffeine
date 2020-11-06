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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Override
    protected void execute(CommandEvent e) {
        // Get Message object from CommandEvent and intiialise arguments as a string array
        Message msg = e.getMessage();
        String[] args = e.getArgs().split("\\s+");

        // Initialise the "company" collection as a MongoDB Collection object
        MongoCollection<Document> companyCol = new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.company);

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

            // Create a Hashmap to hold stock data supplied by the user's stored data
            HashMap<String, Object> tickers = new HashMap<>();
            ArrayList<String[]> embedFields = new ArrayList<>();

            // Method reference used to append the user's portfolio data to the "tickers" document
            portfolio.forEach(tickers::put);

            // Iterates through every entry in the Document and adds the appropriate information to the embed when needed
            tickers.forEach(
                    (ticker, value) -> {
                        // Queries the "company" collection to grab the first company that matches the filter
                        Bson filter = eq("ticker", Pattern.compile(ticker, Pattern.CASE_INSENSITIVE));
                        Document companyData = companyCol.find(filter).first();

                        // Gets the value associated with the "ticker" key
                        Object sharesOwned = portfolio.get(ticker);

                        // Checks if the "sharesOwned" variable is indeed an integer
                        if (sharesOwned instanceof Integer) {
                            // Checks if the company with the ticker value from the ticker key exists
                            if (companyData != null) {
                                // Checks if the user's sharesOwned is a natural number
                                if ((int) sharesOwned >= 1) {
                                    // Adds to the fields document with information on how many shares the user owns
                                    embedFields.add(new String[]{String.format("%1$s (%2$s)", companyData.getString("name"), ticker.toUpperCase()), String.format("%d shares", sharesOwned)});
                                }
                            }
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
            MessageEmbed embed = portfolio_page(user, eb, embedFields, 1).build();
            // Sends the final embed to the user
            Message message = msg.getChannel().sendMessage(embed).complete();

            if (eb.getFields().size() >= 1 && finalUser.equals(e.getAuthor())) {
                message.addReaction("\uD83E\uDD1D").queue();
                transfer_waiter(e, message);
            }

            if (embedFields.size() > 6) {
                message.addReaction("◀️").queue();
                message.addReaction("▶️").queue();
            }


        } catch (NumberFormatException err) {
            // Why is this needed? I have no clue. I should probably remove this try catch...
            msg.getChannel().sendMessage(new Logging().error("OooooOoh no! u gave me and invalid numbwer ;(\n*(Hint: make sure it's a whole number!)*")).queue();
        }
    }

    private EmbedBuilder portfolio_page(User user, EmbedBuilder eb, List<String[]> embedFields, int page) {

        int pages = (int) Maths.Rounding.ceil(embedFields.size() / 6D, 0);

        eb.setAuthor(String.format("AstolfoEx Portfolio (Page %1$d/%2$d)", page, pages), "https://astolfo.tech", user.getAvatarUrl());

        int[] slots = {
                Maths.Equation.astolfoTheory(page, 6, 6),
                Maths.Equation.astolfoTheory(page, 6, 5),
                Maths.Equation.astolfoTheory(page, 6, 4),
                Maths.Equation.astolfoTheory(page, 6, 3),
                Maths.Equation.astolfoTheory(page, 6, 2),
                Maths.Equation.astolfoTheory(page, 6, 1)
        };

        if (embedFields.size() > slots[0]) {
            eb.addField(embedFields.get(slots[0])[0], embedFields.get(slots[0])[1], true);
        }
        if (embedFields.size() > slots[1]) {
            eb.addField(embedFields.get(slots[1])[0], embedFields.get(slots[1])[1], true);
        }
        if (embedFields.size() > slots[2]) {
            eb.addField(embedFields.get(slots[2])[0], embedFields.get(slots[2])[1], true);
        }
        if (embedFields.size() > slots[3]) {
            eb.addField(embedFields.get(slots[3])[0], embedFields.get(slots[3])[1], true);
        }
        if (embedFields.size() > slots[4]) {
            eb.addField(embedFields.get(slots[4])[0], embedFields.get(slots[4])[1], true);
        }
        if (embedFields.size() > slots[5]) {
            eb.addField(embedFields.get(slots[5])[0], embedFields.get(slots[5])[1], true);
        }

        return eb;
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
        Message message = e.getChannel().sendMessage(String.format("**Huh!** datz cool, how many `%s` shares u wantz 2 trade?", companyDoc.getString("name"))).complete();

        waiter.waitForEvent(
                GuildMessageReceivedEvent.class,
                check -> e.getAuthor().getId().equals(check.getAuthor().getId()) && e.getChannel().getId().equals(check.getChannel().getId()),
                action -> {
                    Message msg = action.getMessage();
                    String msg_content = msg.getContentRaw();

                    if (!Maths.Validation.isNumeric(msg_content)) {
                        e.reply("**Right!** thas no numbawh!! numbers look like dis: *1, 2,3 ,4 5, 69 420*\ntry agauinzzz");
                        transfer_amount(e, userShares, companyDoc, ticker);
                        return;
                    }

                    int trade_amount = Integer.parseInt(msg_content);
                    int max_amount = userShares.getInteger(ticker);

                    if (trade_amount <= 0 || trade_amount > max_amount) {
                        e.reply(String.format("**heyyY heYYY!** cmon maaaaaate watttt ya playin at, u know u donz haz `%s` sharesSszzz", msg_content));
                        return;
                    }

                    message.delete().queue();
                    transfer_recipient(e, companyDoc, ticker, trade_amount);
                }
        );
    }

    private void transfer_recipient(CommandEvent e, Document companyDoc, String ticker, int trade_amount) {
        Message message = e.getChannel().sendMessage(String.format("**Mmmk!** who do yaaa leik to send your `%d` share(s) to?\n*u gotta @mention em btw ;P*", trade_amount)).complete();

        waiter.waitForEvent(
                GuildMessageReceivedEvent.class,
                check -> e.getAuthor().getId().equals(check.getAuthor().getId()) && e.getChannel().getId().equals(check.getChannel().getId()),
                action -> {
                    Message msg = action.getMessage();

                    if (msg.getMentionedMembers().size() == 0) {
                        e.reply("bruh.... u didnt even mention anyone omlllllll");
                        return;
                    }

                    User target = msg.getMentionedUsers().get(0);

                    message.delete().queue();
                    transfer_confirm(e, companyDoc, ticker, trade_amount, target);
                }
        );
    }

    private void transfer_confirm(CommandEvent e, Document companyDoc, String ticker, int trade_amount, User target) {
        MessageEmbed embed = new Logging()
                .embed()
                .setAuthor("Confirm Transfer", "https://astolfo.tech", e.getAuthor().getAvatarUrl())
                .setThumbnail(target.getAvatarUrl())
                .setDescription("React with ✅ to confirm the transfer of share(s)\nor you can react with \uD83D\uDEAB to cancel the trade!")
                .addField(companyDoc.getString("name") + String.format(" (%s)", companyDoc.getString("ticker")), String.format("(x%d) shares", trade_amount), false)
                .build();

        Message message = e.getChannel().sendMessage(embed).complete();
        message.addReaction("✅").queue();
        message.addReaction("\uD83D\uDEAB").queue();

        waiter.waitForEvent(
                GuildMessageReactionAddEvent.class,
                check -> e.getAuthor().getIdLong() == check.getUserIdLong() && message.getIdLong() == check.getMessageIdLong(),
                action -> {
                    action.getReaction().removeReaction(action.getUser()).queue();

                    if (action.getReactionEmote().getName().equals("✅")) {
                        Document userStocks = new CloudData().get_data(e.getAuthor().getIdLong(), CloudData.Database.Economy, CloudData.Collection.stocks);
                        Document targetStocks = new CloudData().get_data(target.getIdLong(), CloudData.Database.Economy, CloudData.Collection.stocks);

                        if (!userStocks.containsKey(ticker)) {
                            e.reply(new Logging().error(String.format("Sozzzz u no longer own shares in `%s`\n*(dat means the offer aint valid ;3)*", companyDoc.getString("name"))));
                            return;
                        } else if (userStocks.getInteger(ticker) < trade_amount) {
                            e.reply(new Logging().error(String.format("Sozzzz u no longer own enough shares in `%s`\n*(dat means the offer aint valid ;3)*", companyDoc.getString("name"))));
                            return;
                        }

                        if (targetStocks.containsKey(ticker)) {
                            int oldTargetShareCount = targetStocks.getInteger(ticker);
                            targetStocks.replace(ticker, oldTargetShareCount + trade_amount);
                        } else {
                            targetStocks.append(ticker, trade_amount);
                        }

                        int oldUserShareCount = userStocks.getInteger(ticker);
                        userStocks.replace(ticker, oldUserShareCount - trade_amount);

                        new CloudData().set_data(targetStocks, CloudData.Database.Economy, CloudData.Collection.stocks);
                        new CloudData().set_data(userStocks, CloudData.Database.Economy, CloudData.Collection.stocks);

                        message.delete().queue();
                        e.reply(String.format("**Woooooo!!!** u just sentz *(x%1$d)* sharezz of `%2$s` stock to %3$s", trade_amount, companyDoc.getString("name"), target.getAsMention()));

                    } else if (action.getReactionEmote().getName().equals("\uD83D\uDEAB")) {
                        assert true;

                    } else {
                        transfer_confirm(e, companyDoc, ticker, trade_amount, target);
                    }
                }
        );
    }
}