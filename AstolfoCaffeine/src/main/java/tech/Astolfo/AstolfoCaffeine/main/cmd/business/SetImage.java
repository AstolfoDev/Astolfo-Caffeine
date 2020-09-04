package tech.Astolfo.AstolfoCaffeine.main.cmd.business;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.BasicDBObject;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import org.bson.Document;
import org.bson.conversions.Bson;
import tech.Astolfo.AstolfoCaffeine.App;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.mongodb.client.model.Updates.set;

public class SetImage extends Command {

    private final EventWaiter waiter;
    public SetImage(EventWaiter waiter) {
        super.name = "editcompany";
        super.aliases = new String[]{"edit","setimage","modify","settings"};
        super.help = "set new details for your business";
        super.category = new Category("business");
        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent e) {
        final AtomicBoolean permitted = new AtomicBoolean();
        permitted.set(pistachio(e));

        if (!permitted.get()) return;

        start(e);
    }

    private void start(CommandEvent e) {
        MessageEmbed embed = App.embed(e.getMessage())
            .setAuthor("Company Settings ⚙️", "https://astolfo.tech", e.getAuthor().getAvatarUrl())
            .setDescription("u canz modify various stuffz about ur company riiiiiight here :3")
            .addField("🖼 Logo", "The image associated with your brand!", true)
            .addField("📝 Slogan", "The description to go with your brand!", true)
            .build();
        e.getChannel().sendMessage(embed).queue(
            m -> {
              m.addReaction("🖼").queue();
              m.addReaction("📝").queue();
              menuOption(m, e);
            }
        );
    }

    private void menuOption(Message msg, CommandEvent e) {
      waiter.waitForEvent(
          GuildMessageReactionAddEvent.class, 
          check -> e.getMessage().getAuthor().getIdLong() == check.getUser().getIdLong() && msg.getIdLong() == check.getMessageIdLong(),
          action -> {
            if(action.getReactionEmote().getName().equals("🖼")) {
              msg.delete().queue();
              hazelnut(e);
            } else if (action.getReactionEmote().getName().equals("📝")) {
              msg.delete().queue();
              description(msg, e);
            } else {
              action.getReaction().removeReaction(action.getUser()).queue();
              menuOption(msg, e);
            }
          },
          60, TimeUnit.SECONDS,
          () -> {}
      );
    }

    private boolean pistachio(CommandEvent e) {
        BasicDBObject filter1 = new BasicDBObject("members", new BasicDBObject("$in", Collections.singletonList(e.getMessage().getAuthor().getIdLong())));

        Document comp = App.company.find(filter1).first();

        if (comp == null) {
            e.reply(new Logging().error(e.getSelfUser(), "oi! ur not in a business... create one before changing the logo!!"));
            return false;
        }

        List<Long> admins = (List<Long>) comp.get("admins");
        admins.add(comp.getLong("owner"));

        if (!(admins.contains(e.getAuthor().getIdLong()))) {
            e.reply(new Logging().error(e.getSelfUser(), "oi! ur not an admin or owner of the business, u canzzz change da logo!!"));
            return false;
        }

        return true;
    }

    private AtomicReference<String> hazelnut(CommandEvent e) {
        e.reply("hoiiiiii gimme the image *(as a png/jpg/gif plssZzz)*\nto cancel type `cancel`");

        AtomicReference<String> kitkat = new AtomicReference<>("false");
        List<String> cadbury = Arrays.asList("png","jpeg","jpg","gif");

        waiter.waitForEvent(
                GuildMessageReceivedEvent.class,
                check -> e.getMessage().getAuthor().getIdLong() == check.getAuthor().getIdLong(),
                ev -> {
                    if (ev.getMessage().getAttachments().size() >= 1) {
                        if (cadbury.contains(ev.getMessage().getAttachments().get(0).getFileExtension())) {
                            kitkat.set(ev.getMessage().getAttachments().get(0).getUrl());
                            BasicDBObject filter = new BasicDBObject("members", new BasicDBObject("$in", Collections.singletonList(e.getMessage().getAuthor().getIdLong())));
                            Document comp = almonds(kitkat, filter);
                            e.reply(
                                    App.embed(e.getMessage())
                                            .setAuthor("Company Updated!", "https://astolfo.tech", e.getAuthor().getAvatarUrl())
                                            .setThumbnail(comp.getString("logo"))
                                            .setDescription("Successfully updated the logo for `"+comp.getString("name")+"`")
                                            .build()
                            );
                        } else {
                            hazelnut(e);
                        }
                    } else {
                        if (!(ev.getMessage().getContentRaw().toLowerCase().equals("cancel"))) {
                            hazelnut(e);
                        }
                    }
                },
                60, TimeUnit.SECONDS,
                () -> {}
        );
        return kitkat;
    }

    private Document almonds(AtomicReference<String> kitkat, BasicDBObject filter) {
        Bson update = set("logo", kitkat.get());
        App.company.updateOne(filter, update);
        return App.company.find(filter).first();
    }


    private void description(Message msg, CommandEvent e) {
        String pattern = "(http(s)?:\\/\\/.)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
        e.reply("oi oi gimme dat dessssscription dat u want plezzzzzz <3\nto cancel type `cancel`");

        AtomicReference<String> description = new AtomicReference<>();
        waiter.waitForEvent(
                    GuildMessageReceivedEvent.class,
                    check -> e.getMessage().getAuthor().getIdLong() == check.getAuthor().getIdLong() && e.getChannel().getIdLong() == check.getMessage().getChannel().getIdLong(),
                    action -> {
                        final String desc = action.getMessage().getContentRaw().replaceAll(pattern, "");
                        if (desc.equalsIgnoreCase("cancel")) {
                          e.reply("understandable. have a nice day! uwu~ <3");
                          return;
                        }
                        if (desc.length() > 140) {
                            e.reply(new Logging().error(e.getSelfUser(), "**sozZz!** ur desc can only be 140 characterz long!!! ;P"));
                            return;
                        } 
                        description.set(desc);
                        BasicDBObject filter = new BasicDBObject("members", new BasicDBObject("$in", Collections.singletonList(e.getMessage().getAuthor().getIdLong())));
                        Bson update = set("description", description.get());
                        App.company.updateOne(filter, update);
                        Document comp = App.company.find(filter).first();
                        e.reply(
                                    App.embed(e.getMessage())
                                            .setAuthor("Company Updated!", "https://astolfo.tech", e.getAuthor().getAvatarUrl())
                                            .setThumbnail(comp.getString("logo"))
                                            .setDescription("Successfully updated the description for `"+comp.getString("name")+"`\n\n"+comp.getString("description"))
                                            .build()
                        );
                    },
                    60, TimeUnit.SECONDS,
                    () -> {
                    }
        );
        
    }
}