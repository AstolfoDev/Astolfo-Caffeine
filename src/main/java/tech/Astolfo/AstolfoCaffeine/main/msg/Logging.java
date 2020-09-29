package tech.Astolfo.AstolfoCaffeine.main.msg;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import tech.Astolfo.AstolfoCaffeine.App;

import javax.annotation.Nullable;
import java.util.Arrays;

public class Logging {
    public MessageEmbed error(String str) {
        return new EmbedBuilder()
          .setTitle(App.jda.getSelfUser().getName()+" had an uh-oh moment!")
          .setFooter(System.getenv("VERSION_ID"), App.avatarURL)
          .setDescription(str)
          .setColor(0xde1073)
          .build();
    }

    public EmbedBuilder embed() {
        return new EmbedBuilder()
                .setFooter(System.getenv("VERSION_ID"), App.avatarURL)
                .setColor(0xde1073);
    }

    public MessageEmbed send(@Nullable Message msg, @Nullable String description, @Nullable String author, @Nullable String thumbnail, @Nullable String[]... params) {

        // The EmbedBuilder is initialised
        EmbedBuilder builder = embed()
                .setDescription(description)
                .setThumbnail(thumbnail)
                .setAuthor(author, "https://astolfo.tech");


        // Creates new fields for all the field parameters
        Arrays.stream(params).forEach(
                elem -> {
                    // Checks if element is null
                    if (elem == null || elem.length != 3) return;

                    // Adds field to EmbedBuilder
                    builder.addField(elem[0], elem[1], Boolean.parseBoolean(elem[2]));
                }
        );


        // The EmbedBuilder is converted into a MessageEmbed
        MessageEmbed embed = builder.build();


        // Checks if the message parameter was given and sends the embed if it was
        if (msg != null) {

            // Replaces the embed author's icon with the avatar URL of the command issuer
            MessageEmbed.AuthorInfo info = embed.getAuthor();
            assert info != null;
            embed = builder.setAuthor(info.getName(), info.getUrl(), msg.getAuthor().getAvatarUrl()).build();

            // Sends the embed in the channel where the command was issued
            msg.getChannel().sendMessage(embed).queue();
        }


        return embed;
    }
}