package tech.Astolfo.AstolfoCaffeine.main.msg;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;

public class Logging {
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
