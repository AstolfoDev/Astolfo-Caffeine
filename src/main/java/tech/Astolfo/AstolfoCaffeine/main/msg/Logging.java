package tech.Astolfo.AstolfoCaffeine.main.msg;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import tech.Astolfo.AstolfoCaffeine.App;

public class Logging {
    public MessageEmbed error(String str) {
        return new EmbedBuilder()
          .setTitle(App.jda.getSelfUser().getName()+" had an uh-oh moment!")
          .setFooter(System.getenv("VERSION_ID"), App.avatarURL)
          .setDescription(str)
          .setColor(0xde1073)
          .build();
    }
}