package tech.Astolfo.AstolfoCaffeine.main.cmd.info;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import tech.Astolfo.AstolfoCaffeine.App;
import tech.Astolfo.AstolfoCaffeine.main.cmd.business.Hire;

public class Invite extends Command {

    public Invite() {
        super.name = "invite";
        super.aliases = new String[]{"info","get","astolfo"};
        super.help = "get Astolfo on your server";
        super.category = new Category("info");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (!e.getArgs().isBlank()) {
          new Hire().run(e);
          return;
        }
        Message msg = e.getMessage();
        User author = msg.getAuthor();

        String invite = e.getJDA().getInviteUrl(Permission.getPermissions(2080374975));
        MessageEmbed embed = App.embed()
            .setAuthor("Get Astolfo!", invite, author.getAvatarUrl())
            .setDescription("heWwwwo! i'm Astolfo, the cute and quirky Discord bot for all your needs!")
            .addField("Invitation Link", "[Click me! :3]("+invite+")", false)
            .build();
        
        e.reply(embed);
    }
}