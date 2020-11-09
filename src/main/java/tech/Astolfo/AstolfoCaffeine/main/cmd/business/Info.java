package tech.Astolfo.AstolfoCaffeine.main.cmd.business;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.bson.Document;
import org.bson.conversions.Bson;
import tech.Astolfo.AstolfoCaffeine.main.db.CloudData;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;

public class Info extends Command {

    private final EventWaiter waiter;

    public Info(EventWaiter waiter) {
        super.name = "company";
        super.aliases = new String[]{"inf","companieshouse","checkinfo","check"};
        super.help = "view the information of a company";
        super.arguments = "<company>";
        super.category = new Category("business");
        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent e) {
        MongoCollection<Document> company = new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.company);
        MongoCollection<Document> stocks = new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.stocks);

        try {
            Message msg = e.getMessage();

            String name = e.getArgs();
            if (name.equals("")) {
                e.reply(new Logging().error("**sowwwwwwwy!!!** u fOwOgot 2 specify the company u wanz to checkzzZZz lmao ;3"));
                return;
            }

            Bson filter = eq("name", Pattern.compile(name, Pattern.CASE_INSENSITIVE));
            Document docs = company.find(filter).first();

            if (name.length() > 16) {
                e.reply(new Logging().error("invawwwwid company nayyyyme ;/"));
                return;
            }

            if (docs == null) {
                e.reply(new Logging().error("AHEAFHBEAUF nuuuu ;( no company matched the query: `" + name + "`"));
                return;
            }

            List<Long> members = (List<Long>) docs.get("members");
            List<Long> admins = (List<Long>) docs.get("admins");

            final MessageEmbed p1;
            final MessageEmbed p2;
            final MessageEmbed p3;

            final int page = 1;
            final int pages;

            if (docs.containsKey("ticker")) {
                String ticker = docs.getString("ticker");
                int shares = 0;
                int shareholders = 0;
                pages = 3;
                Document highest = null;
                for (Document doc : stocks.find()) {
                    if (highest == null) {
                        highest = doc;
                    } else if (highest.getInteger(ticker) < doc.getInteger(ticker)) {
                        highest = doc;
                    }
                    shares += doc.getInteger(ticker);
                    if (doc.getInteger(ticker) >= 1) {
                        shareholders += 1;
                    }
                }
                assert highest != null;

                p3 = new Logging().embed()
                        .setAuthor(docs.getString("name") + " (3/" + pages + ")", "https://astolfo.tech", docs.getString("logo"))
                        .setThumbnail(docs.getString("logo"))
                        .setDescription("AstolfoEx Stats")
                        .addField("Majority Shareholder", e.getJDA().retrieveUserById(highest.getLong("userID")).complete().getAsMention(), true)
                        .addField("Shareholders", String.valueOf(shareholders), true)
                        .addField("Shares Outstanding", String.valueOf(shares), true)
                        .build();
            } else {
                p3 = null;
                pages = 2;
            }

            MessageEmbed page1 = new Logging().embed()
                    .setAuthor(docs.getString("name") + " (1/" + pages + ")", "https://astolfo.tech", docs.getString("logo"))
                    .setThumbnail(docs.getString("logo"))
                    .setDescription(docs.getString("description"))
                    .addField("CEO", e.getJDA().retrieveUserById(docs.getLong("owner")).complete().getAsMention(), true)
                    .addField("Employees", String.valueOf(members.size()), true)
                    .addField("Sales", docs.get("xp").toString(), true)
                    .build();

            MessageEmbed page2 = new Logging().embed()
                    .setAuthor(docs.getString("name") + " (2/" + pages + ")", "https://astolfo.tech", docs.getString("logo"))
                    .setThumbnail(docs.getString("logo"))
                    .setDescription("Board of Directors")
                    .addField("Chairman & CEO", e.getJDA().retrieveUserById(docs.getLong("owner")).complete().getAsMention(), true)
                    .addField("Directors", String.valueOf(admins.size()), true)
                    .build();

            p1 = page1;
            p2 = page2;

            msg.getChannel().sendMessage(page1).queue(
                    m -> {
                        m.addReaction("◀️").queue();
                        m.addReaction("▶️").queue();
                        getReaction(msg, m, page, pages, p1, p2, p3);
                    }
            );
        } catch (ErrorResponseException err) {
            e.replyError("**Sorry!** An unexpected error occured, please contact an Astolfo administrator to repair your company data.");
        }
    }

    private void getReaction(Message msg, Message m, int page, int pages, MessageEmbed page1, MessageEmbed page2, @Nullable MessageEmbed page3) {
        waiter.waitForEvent(
                GuildMessageReactionAddEvent.class,
                check -> msg.getAuthor().getIdLong() == check.getUserIdLong() && m.getIdLong() == check.getMessageIdLong(),
                e -> {
                    final int p;
                    List<String> emotes = Arrays.asList("◀️", "▶️");
                    if (!emotes.contains(e.getReactionEmote().getEmoji())) return;
                    if (e.getReactionEmote().getEmoji().equals("◀️")) {
                        e.getReaction().removeReaction(msg.getAuthor()).queue();
                        if (page == 1) {
                            getReaction(msg, m, page, pages, page1, page2, page3);
                            return;
                        }
                        p = page-1;
                    } else {
                        e.getReaction().removeReaction(msg.getAuthor()).queue();
                        if (page == pages) {
                            getReaction(msg, m, page, pages, page1, page2, page3);
                            return;
                        }
                        p = page+1;
                    }

                    if (p == 1) {
                        m.editMessage(page1).queue();
                    } else if (p == 2) {
                        m.editMessage(page2).queue();
                    } else if (p == 3) {
                        assert page3 != null;
                        m.editMessage(page3).queue();
                    }
                    getReaction(msg, m, p, pages, page1, page2, page3);
                },
                60, TimeUnit.SECONDS,
                () -> {});
    }
}