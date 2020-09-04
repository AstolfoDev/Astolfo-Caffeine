package tech.Astolfo.AstolfoCaffeine.main.msg;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;

public class Logging {

    private EmbedBuilder embed;
    private CommandEvent ctx;
    
    public Logging() {
    }
    
    public Logging(CommandEvent _ctx) {
        ctx = _ctx;
        embed = new EmbedBuilder()
                .setTitle(ctx.getSelfUser().getName()+" had an uh-oh moment!")
                .setFooter(System.getenv("VERSION_ID"), ctx.getSelfUser().getAvatarUrl())
                .setColor(0xde1073);
    }

    public void error(String msg) {
        ctx.getChannel().sendMessage(embed.setDescription(msg).build()).queue();
    }

    public MessageEmbed error(SelfUser client, String str) {
        MessageEmbed e = new EmbedBuilder()
          .setTitle(client.getName()+" had an uh-oh moment!")
          .setFooter(System.getenv("VERSION_ID"), client.getAvatarUrl())
          .setDescription(str)
          .setColor(0xde1073)
          .build();
        return e;
    }


}
