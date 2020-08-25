package tech.Astolfo.AstolfoCaffeine.main.cmd.business;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.BasicDBObject;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
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
        super.name = "setimage";
        super.aliases = new String[]{"si","image"};
        super.help = "set a new image for your business";
        super.category = new Category("business");
        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent e) {
        final AtomicBoolean permitted = new AtomicBoolean();
        permitted.set(pistachio(e));

        if (!permitted.get()) return;

        AtomicReference<String> kitkat = new AtomicReference<>();
        kitkat.set(hazelnut(e).get());
        System.out.println(kitkat.get());
        if (kitkat.get().equals("false")) return;

        BasicDBObject filter = new BasicDBObject("members", new BasicDBObject("$in", Collections.singletonList(e.getMessage().getAuthor().getIdLong())));

        Document comp = almonds(kitkat, filter);
        e.reply(
                App.embed(e.getMessage())
                        .setAuthor("Company Updated!", "https://astolfo.tech", e.getAuthor().getAvatarUrl())
                        .setThumbnail(comp.getString("logo"))
                        .setDescription("Successfully updated the logo for `"+comp.getString("name")+"`")
                        .build()
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
        e.reply("oi dickhead gimme the image\nto cancel type `cancel`");

        AtomicReference<String> kitkat = new AtomicReference<>("false");
        List<String> cadbury = Arrays.asList("png","jpeg","jpg","gif");

        waiter.waitForEvent(
                GuildMessageReceivedEvent.class,
                check -> e.getMessage().getAuthor().getIdLong() == check.getAuthor().getIdLong(),
                ev -> {
                    if (ev.getMessage().getAttachments().size() >= 1) {
                        if (cadbury.contains(ev.getMessage().getAttachments().get(0).getFileExtension())) {
                            kitkat.set(ev.getMessage().getAttachments().get(0).getUrl());
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
        Bson update = set("bson", kitkat.get());
        App.company.updateOne(filter, update);
        return App.company.find(filter).first();
    }
}