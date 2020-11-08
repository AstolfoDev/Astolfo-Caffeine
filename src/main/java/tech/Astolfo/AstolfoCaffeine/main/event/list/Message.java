package tech.Astolfo.AstolfoCaffeine.main.event.list;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bson.Document;
import tech.Astolfo.AstolfoCaffeine.main.db.CloudData;
import tech.Astolfo.AstolfoCaffeine.main.util.caching.Cache;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Message {
    public void run(MessageReceivedEvent e) {
        astolfoAPI(e);
        //mentioned(e);
        //prefixHandler(e.getGuild(), e.getMessage().getContentRaw(), e);
    }

    private void astolfoAPI(MessageReceivedEvent e) {
        User author = e.getAuthor();

        if (author.getId().equals(e.getJDA().getSelfUser().getId())) {
            if (e.getMessage().getContentRaw().equals("online")) {
                e.getChannel().sendMessage("online true").queue(m -> m.delete().queue());
            }
        }
    }

    private void mentioned(MessageReceivedEvent e) {
        String msg = e.getMessage().getContentRaw();
        User selfUser = e.getJDA().getSelfUser();

        if (msg.equals("<@" + selfUser.getId() + ">") || msg.equals("<@!" + selfUser.getId() + ">")) {
            e.getChannel().sendMessage(
                    String.format(
                            "Hewwo! *immmm Astolfo!!!11!*\nMaiiiii prefix on dis server izzz `%s`",
                            getPrefix(e.getGuild())
                    )
            ).complete();
        }
    }

    private String getPrefix(Guild guild) {
        Document guildSettings = new CloudData().get_data(guild.getIdLong(), CloudData.Database.Guilds, CloudData.Collection.guild_settings);
        if (guildSettings == null) return System.getenv("PREFIX");

        return guildSettings.getString("prefix");
    }

    private void prefixHandler(Guild guild, String msg, MessageReceivedEvent e) {

        String prefix = getPrefix(guild);

        String[] cmd_query = msg.split("\\s+");

        String command = cmd_query[0].replace(prefix, "");
        String arguments = msg.replaceAll(prefix + command + "+\\s+", "").replace(prefix + command, "");

        CommandClient client = Cache.client;
        List<Command> commands = client.getCommands();

        AtomicBoolean found = new AtomicBoolean(false);
        AtomicReference<Command> cmd_obj = new AtomicReference<>();

        commands.forEach(cmd -> {
            if (cmd.getName().toLowerCase().equals(command.toLowerCase())) {
                found.set(true);
                cmd_obj.set(cmd);
            } else if (Arrays.asList(cmd.getAliases()).contains(command.toLowerCase())) {
                found.set(true);
                cmd_obj.set(cmd);
            }
        });

        if (found.get()) {
            CommandEvent event = new CommandEvent(e, arguments, client);
            cmd_obj.get().run(event);
        }
    }
}