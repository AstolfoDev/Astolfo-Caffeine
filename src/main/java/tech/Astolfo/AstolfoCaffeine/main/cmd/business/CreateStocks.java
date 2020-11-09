package tech.Astolfo.AstolfoCaffeine.main.cmd.business;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.bson.Document;
import org.bson.conversions.Bson;
import tech.Astolfo.AstolfoCaffeine.main.db.CloudData;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;
import tech.Astolfo.AstolfoCaffeine.main.util.maths.Maths;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;

public class CreateStocks extends Command {

    private final EventWaiter waiter;

    public CreateStocks(EventWaiter waiter) {
        super.name = "createstocks";
        super.aliases = new String[]{"createstock", "goprivate"};
        super.help = "create shares for your company to trade!";
        super.category = new Category("business");

        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent e) {
        User author = e.getAuthor();
        MessageChannel channel = e.getChannel();

        Document companyData = new CloudData().get_data(author.getIdLong(), CloudData.Database.Economy, CloudData.Collection.company);

        if (!validateUser(e, companyData, author)) return;

        EmbedBuilder embed = new Logging().embed()
                .setAuthor("How many shares would you like to start with?")
                .setDescription("Type `cancel` to end this process");

        Message message = e.getChannel()
                .sendMessage(embed.build())
                .complete();

        startProcess(e);
    }

    private boolean validateUser(CommandEvent e, Document companyData, User author) {
        if (companyData == null) {
            e.getChannel().sendMessage(new Logging().error("You don't belong to a company!")).queue();
            return false;

        } else if (companyData.containsKey("ticker")) {
            e.getChannel()
                    .sendMessage(
                            new Logging().error(
                                    String.format(
                                            "`%s` already has a ticker and registered shares!",
                                            companyData.getString("name")
                                    )
                            )
                    )
                    .queue();
            return false;

        } else if (!companyData.getLong("owner").equals(author.getIdLong())) {
            e.getChannel()
                    .sendMessage(
                            new Logging().error(
                                    String.format(
                                            "You are not the owner of `%s`!",
                                            companyData.getString("name")
                                    )
                            )
                    ).queue();
            return false;

        }

        return true;
    }

    private boolean validateTicker(String ticker) {
        if (ticker.length() < 1 || ticker.length() > 5) return false; // Check if valid character length
        if (!ticker.chars().allMatch(chr -> chr >= 0x20 && chr < 0x7F)) return false; // Check if printable ASCII

        MongoCollection<Document> companyCol = new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.company);
        final Bson filter = eq("ticker", Pattern.compile(ticker, Pattern.CASE_INSENSITIVE));

        return companyCol.find(filter).first() == null; // Check if ticker is already in use
    }

    private void startProcess(CommandEvent e) {
        waiter.waitForEvent(
                GuildMessageReceivedEvent.class,
                check -> check.getChannel().getId().equals(e.getChannel().getId()) && check.getAuthor().getId().equals(e.getAuthor().getId()),
                action -> {
                    String msg = action.getMessage().getContentRaw();
                    if (msg.equalsIgnoreCase("cancel")) {
                        e.reply(String.format("**Cancelled!** Okaiiii den %s, i gotchu!", e.getAuthor().getAsMention()));
                        return;
                    }

                    if (!Maths.Validation.isNumeric(msg)) {
                        action.getMessage().delete().complete();
                        e.reply("**Hey!!** datz not a number, try againzzz!");
                        startProcess(e);
                        return;
                    }

                    int maxValue = 999999;

                    if (msg.length() > 6) {
                        e.reply("**Emmm...** ur shares can't exceed `999,999`\n*go again... pls ahaha!!*");
                        startProcess(e);
                        return;
                    }

                    int shares = Integer.parseInt(msg);

                    if (shares == 0) {
                        e.reply("no... lmfao\n*try again please ahahaha*");
                        startProcess(e);
                        return;
                    }

                    EmbedBuilder embed = new Logging().embed()
                            .setAuthor("What would you like your ticker to be?")
                            .setDescription("[Ticker Requirements](https://smug.astolfo.tech/sites/astolfo/bot/docs/ancap/astolfoex/tickerreq.html)\nType `cancel` to end this process");

                    e.getChannel()
                            .sendMessage(embed.build())
                            .complete();

                    getTicker(e, shares);
                },
                25,
                TimeUnit.SECONDS,
                () -> {
                    e.reply("**OH!!** U rannnnz outta dat timez to repllly llol");
                }
        );
    }

    private void getTicker(CommandEvent e, int shares) {
        waiter.waitForEvent(
                GuildMessageReceivedEvent.class,
                check -> check.getChannel().getId().equals(e.getChannel().getId()) && check.getAuthor().getId().equals(e.getAuthor().getId()),
                action -> {
                    User author = action.getAuthor();
                    String ticker = action.getMessage().getContentRaw().toUpperCase();

                    if (ticker.equalsIgnoreCase("cancel")) {
                        e.reply(String.format("**Cancelled!** Okaiiii den %s, i gotchu!", e.getAuthor().getAsMention()));
                        return;
                    }

                    if (!validateTicker(ticker)) {
                        e.reply("**OI!** Dat ticker doesn't meet da requirements!\n*Tryyyyyy again!!*");
                        getTicker(e, shares);
                        return;
                    }

                    Document companyData = new CloudData().get_data(author.getIdLong(), CloudData.Database.Economy, CloudData.Collection.company);
                    if (!validateUser(e, companyData, author)) return;

                    Bson filter = new BasicDBObject("members", new BasicDBObject("$in", Collections.singletonList(author.getIdLong())));

                    MongoCollection<Document> companyCol = new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.company);
                    Document updateDoc = new Document().append("$set", new Document().append("ticker", ticker.toLowerCase()));
                    companyCol.updateOne(filter, updateDoc);

                    Document userShares = new CloudData().get_data(author.getIdLong(), CloudData.Database.Economy, CloudData.Collection.stocks);

                    userShares.remove(ticker);
                    userShares.append(ticker, shares);

                    new CloudData().set_data(userShares, CloudData.Database.Economy, CloudData.Collection.stocks);

                    MessageEmbed embed = new Logging().embed()
                            .setAuthor("You've got shares!", "https://astolfo.tech", action.getAuthor().getAvatarUrl())
                            .setThumbnail(companyData.getString("logo"))
                            .setDescription(String.format(
                                    "Successfully created `%1$d` shares for `%2$s` under the ticker symbol `%3$s`",
                                    shares,
                                    companyData.getString("name"),
                                    ticker
                            ))
                            .build();

                    e.reply(embed);
                },
                45,
                TimeUnit.SECONDS,
                () -> {
                    e.reply("**ERMMMZ!!** u took far too long to reply.... i cant wait here all day yknowz");
                }
        );
    }
}
