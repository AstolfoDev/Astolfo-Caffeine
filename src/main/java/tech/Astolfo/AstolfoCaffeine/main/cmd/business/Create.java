package tech.Astolfo.AstolfoCaffeine.main.cmd.business;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.bson.Document;
import org.bson.conversions.Bson;
import tech.Astolfo.AstolfoCaffeine.main.db.CloudData;
import tech.Astolfo.AstolfoCaffeine.main.db.Database;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;

public class Create extends Command {

    private final EventWaiter waiter;
    public Create(EventWaiter waiter) {
        super.name = "create";
        super.aliases = new String[]{"new","start"};
        super.help = "create a new company!";
        super.category = new Category("business");
        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent e) {

        BasicDBObject filter1 = new BasicDBObject("members", new BasicDBObject("$in", Collections.singletonList(e.getMessage().getAuthor().getIdLong())));

        MongoCollection<Document> company = new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.company);
        Document comp = company.find(filter1).first();

        if (comp != null) {
            e.reply(new Logging().error("oi! ur already in a business... leave it before creating a new one!!"));
            return;
        }

        String pattern = "(http(s)?:\\/\\/.)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
        List<String> validExt = Arrays.asList("png", "jpeg", "jpg", "gif");

        e.reply("wut would u leik to call ur business?");
        waiter.waitForEvent(
                GuildMessageReceivedEvent.class,
                e2 -> e.getMessage().getAuthor().getIdLong() == e2.getAuthor().getIdLong(),
                e2 -> {
                    if (e2.getMessage().getContentRaw().length() > 16) {
                        e.reply(new Logging().error("**SoowWy!** the name can only be 16 characters long or less..."));
                        return;
                    }
                    final String name = e2.getMessage().getContentRaw();
                    final Bson filter = eq("name", Pattern.compile(name, Pattern.CASE_INSENSITIVE));

                    if (company.find(filter).first() != null) {
                        e.reply(new Logging().error("**EEhhHHhh!!** u sorta like canz choose that name cuzzzz some1 else alreadyyyy haz it ;( ;( ;("));
                        return;
                    }

                    e.reply("ight, what would u like the description to say?");
                    waiter.waitForEvent(
                            GuildMessageReceivedEvent.class,
                            e3 -> e.getMessage().getAuthor().getIdLong() == e3.getAuthor().getIdLong(),
                            e3 -> {
                                final String description = e3.getMessage().getContentRaw().replaceAll(pattern, "");
                                if (description.length() > 140) {
                                    e.reply(new Logging().error("**sozZz!** ur desc can only be 140 characterz long!!! ;P"));
                                    return;
                                }
                                e.reply("upload a logo for ur business here pls!");

                                waiter.waitForEvent(
                                        GuildMessageReceivedEvent.class,
                                        e4 -> e.getMessage().getAuthor().getIdLong() == e4.getAuthor().getIdLong(),
                                        e4 -> {
                                            if (e4.getMessage().getAttachments().size() > 1) {
                                                e.reply(new Logging().error("too many attatchments sent! using default logo!"));
                                                final String logo = "https://media.discordapp.net/attachments/738514936338055178/745569159550730280/flat750x075f-pad750x1000f8f8f8.u3.jpg";
                                                finalCalc(e4.getMessage(), name, description, logo);
                                            } else if (e4.getMessage().getAttachments().size() == 1) {
                                                final String ext = e4.getMessage().getAttachments().get(0).getFileExtension();
                                                assert ext != null;
                                                if (validExt.contains(ext.toLowerCase())) {
                                                    final String logo = e4.getMessage().getAttachments().get(0).getUrl();
                                                    finalCalc(e4.getMessage(), name, description, logo);
                                                } else {
                                                    e.reply(new Logging().error("invalid image type! using default logo!"));
                                                    final String logo = "https://media.discordapp.net/attachments/738514936338055178/745569159550730280/flat750x075f-pad750x1000f8f8f8.u3.jpg";
                                                    finalCalc(e4.getMessage(), name, description, logo);
                                                }
                                            } else {
                                                e.reply(new Logging().error("no image sent! using default logo!"));
                                                final String logo = "https://media.discordapp.net/attachments/738514936338055178/745569159550730280/flat750x075f-pad750x1000f8f8f8.u3.jpg";
                                                finalCalc(e4.getMessage(), name, description, logo);
                                            }
                                        },
                                        60, TimeUnit.SECONDS,
                                        () -> {
                                        }
                                );
                            }
                    );
                },
                60, TimeUnit.SECONDS,
                () -> {
                }
        );
    }

    private void finalCalc(Message msg, String name, String desc, String logo) {
        new Database().create_company(name, desc, logo, msg.getAuthor().getIdLong());
        EmbedBuilder eb = new Logging().embed();
        MessageEmbed embed = eb
                .setAuthor("Company created!", "https://astolfo.tech", msg.getAuthor().getAvatarUrl())
                .setThumbnail(logo)
                .addField("Name", name, true)
                .addField("Description", desc, true)
                .addField("Industry", "N/A", true)
                .build();
        msg.getChannel().sendMessage(embed).queue();
    }
}