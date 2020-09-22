package tech.Astolfo.AstolfoCaffeine.main.cmd.info;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import tech.Astolfo.AstolfoCaffeine.App;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Help extends Command {

    private EventWaiter waiter;
    public Help(EventWaiter waiter) {
        super.name = "Help";
        super.hidden = true;
        super.arguments = "<command/category>";
        super.aliases = new String[]{"?","sendhelp","helpme","tasukete"};
        super.help = "**I N C E P T I O N**";
        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent e) {
        String[] args = e.getArgs().split("\\s+");
        if (args.length >= 1 && !args[0].equals("")) command(e, args); else noArgs(e);
    }

    private void command(CommandEvent e, String[] args) {
        CommandClient client = e.getClient();
        List<Command> commands = client.getCommands();

        AtomicBoolean found = new AtomicBoolean(false);
        AtomicReference<Command> command = new AtomicReference<>();
        commands.forEach(cmd -> {
            if (cmd.getName().toLowerCase().equals(args[0].toLowerCase())) {
                found.set(true);
                command.set(cmd);
            } else if (Arrays.asList(cmd.getAliases()).contains(args[0].toLowerCase())) {
                found.set(true);
                command.set(cmd);
            }
        });

        if (!found.get()) {
            e.reply(new Logging().error(e.getSelfUser(), "**h-huh!?** i canz find dat command!!!"));
        } else {
            EmbedBuilder eb = App.embed()
                    .setAuthor(command.get().getName().substring(0, 1).toUpperCase()+command.get().getName().substring(1)+" Command", "https://astolfo.tech", e.getAuthor().getAvatarUrl())
                    .setDescription(command.get().getHelp().substring(0, 1).toUpperCase()+command.get().getHelp().substring(1))
                    .addField("Aliases", String.join(", ", command.get().getAliases()), false);

            if (!(command.get().getCategory() == null)) {
                eb.addField("Category", Objects.requireNonNull((command.get().getCategory().getName().substring(0, 1).toUpperCase()+command.get().getCategory().getName().substring(1))), false);
            }
            if (!(command.get().getArguments() == null)) {
                eb.appendDescription("\n`"+command.get().getName()+" "+command.get().getArguments()+"`");
            }
            
            MessageEmbed embed = eb.build();
            e.reply(embed);
        }
    }

    private void noArgs(CommandEvent e) {
        CommandClient client = e.getClient();
        List<Command> commands = client.getCommands();

        List<Command> info = new ArrayList<>();
        List<Command> gambling = new ArrayList<>();
        List<Command> economy = new ArrayList<>();
        List<Command> business = new ArrayList<>();
        List<Command> ancap = new ArrayList<>();

        commands.forEach(
                cmd -> {
                    if (cmd.getCategory() != null) {
                        switch (cmd.getCategory().getName()) {
                            case "info":
                                info.add(cmd);
                                break;
                            case "gambling":
                                gambling.add(cmd);
                                break;
                            case "economy":
                                economy.add(cmd);
                                break;
                            case "business":
                                business.add(cmd);
                                break;
                            case "ancap":
                                ancap.add(cmd);
                                break;
                        }
                    }
                }
        );

        AtomicInteger page = new AtomicInteger();
        AtomicInteger pages = new AtomicInteger();

        page.set(1);
        pages.set(5);

        EmbedBuilder infoPage = App.embed()
                .setAuthor("Help Command (1/"+pages.get()+")", "https://astolfo.tech", e.getAuthor().getAvatarUrl())
                .setThumbnail("https://cdn.discordapp.com/attachments/738514936338055178/746872226527182848/Screenshot_2020-08-23_at_00.23.22.png")
                .setDescription("Info Category");
        info.forEach(
                cmd -> infoPage.addField(cmd.getName().substring(0, 1).toUpperCase()+cmd.getName().substring(1), cmd.getHelp().substring(0, 1).toUpperCase()+cmd.getHelp().substring(1), true)
        );
        MessageEmbed inf = infoPage.build();

        EmbedBuilder gamblingPage = App.embed()
                .setAuthor("Help Command (2/"+pages.get()+")", "https://astolfo.tech", e.getAuthor().getAvatarUrl())
                .setThumbnail("https://cdn.discordapp.com/attachments/738514936338055178/746868751353511976/image0.png")
                .setDescription("Gambling Category");
        gambling.forEach(
                cmd -> gamblingPage.addField(cmd.getName().substring(0, 1).toUpperCase()+cmd.getName().substring(1), cmd.getHelp().substring(0, 1).toUpperCase()+cmd.getHelp().substring(1), true)
        );
        MessageEmbed gam = gamblingPage.build();

        EmbedBuilder economyPage = App.embed()
                .setAuthor("Help Command (3/"+pages.get()+")", "https://astolfo.tech", e.getAuthor().getAvatarUrl())
                .setThumbnail("https://cdn.discordapp.com/attachments/738514936338055178/746869445342920824/Screenshot_2020-08-23_at_00.12.19.png")
                .setDescription("Economy Category");
        economy.forEach(
                cmd -> economyPage.addField(cmd.getName().substring(0, 1).toUpperCase()+cmd.getName().substring(1), cmd.getHelp().substring(0, 1).toUpperCase()+cmd.getHelp().substring(1), true)
        );
        MessageEmbed eco = economyPage.build();

        EmbedBuilder businessPage = App.embed()
                .setAuthor("Help Command (4/"+pages.get()+")", "https://astolfo.tech", e.getAuthor().getAvatarUrl())
                .setThumbnail("https://cdn.discordapp.com/attachments/738514936338055178/746869868581879879/Screenshot_2020-08-23_at_00.14.00.png")
                .setDescription("Business Category");
        business.forEach(
                cmd -> businessPage.addField(cmd.getName().substring(0, 1).toUpperCase()+cmd.getName().substring(1), cmd.getHelp().substring(0, 1).toUpperCase()+cmd.getHelp().substring(1), true)
        );
        MessageEmbed bus = businessPage.build();

        EmbedBuilder ancapPage = App.embed()
                .setAuthor("Help Command (5/"+pages.get()+")", "https://astolfo.tech", e.getAuthor().getAvatarUrl())
                .setThumbnail("https://cdn.discordapp.com/attachments/738514936338055178/746872045085786143/Screenshot_2020-08-23_at_00.22.28.png")
                .setDescription("Capitalism Category");
        ancap.forEach(
                cmd -> ancapPage.addField(cmd.getName().substring(0, 1).toUpperCase()+cmd.getName().substring(1), cmd.getHelp().substring(0, 1).toUpperCase()+cmd.getHelp().substring(1), true)
        );
        MessageEmbed anc = ancapPage.build();

        e.getChannel().sendMessage(inf).queue(
                m -> {
                    m.addReaction("◀️").queue();
                    m.addReaction("▶️").queue();
                    pageHandler(e.getMessage(), m, page, pages, inf, gam, eco, bus, anc);
                }
        );
    }

    private void pageHandler(Message msg, Message m, AtomicInteger page, AtomicInteger pages, MessageEmbed inf, MessageEmbed gam, MessageEmbed eco, MessageEmbed bus, MessageEmbed anc) {
        waiter.waitForEvent(
                GuildMessageReactionAddEvent.class,
                check -> msg.getAuthor().getIdLong() == check.getUserIdLong() && m.getIdLong() == check.getMessageIdLong(),
                e -> {
                    if (e.getReactionEmote().getEmoji().equals("◀️")) {
                        if (page.get() == 1) {
                            pageHandler(msg, m, page, pages, inf, gam, eco, bus, anc);
                            return;
                        }
                        e.getReaction().removeReaction(e.getUser()).queue();
                        page.getAndAdd(-1);
                    } else if (e.getReactionEmote().getEmoji().equals("▶️")) {
                        if (page.get() == pages.get()) {
                            pageHandler(msg, m, page, pages, inf, gam, eco, bus, anc);
                            return;
                        }
                        e.getReaction().removeReaction(e.getUser()).queue();
                        page.getAndAdd(1);
                    } else {
                        pageHandler(msg, m, page, pages, inf, gam, eco, bus, anc);
                        return;
                    }
                    switch(page.get()) {
                        case 1:
                            m.editMessage(inf).queue();
                            break;
                        case 2:
                            m.editMessage(gam).queue();
                            break;
                        case 3:
                            m.editMessage(eco).queue();
                            break;
                        case 4:
                            m.editMessage(bus).queue();
                            break;
                        case 5:
                            m.editMessage(anc).queue();
                            break;
                    }
                    pageHandler(msg, m, page, pages, inf, gam, eco, bus, anc);
                },
                60, TimeUnit.SECONDS,
                () -> {}
        );
    }
}