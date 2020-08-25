package tech.Astolfo.AstolfoCaffeine.main.cmd.economy;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import org.bson.Document;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import tech.Astolfo.AstolfoCaffeine.main.db.Database;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;

public class Balance extends Command {

    public Balance() {
        super.name = "balance";
        super.aliases = new String[]{"bal","money","wallet","cash"};
        super.help = "view your eco balance";
        super.category = new Category("economy");
    }

    @Override
    protected void execute(CommandEvent e) {
        Message msg = e.getMessage();
        User u = msg.getAuthor();
        if (msg.getMentionedUsers().size() >= 1) {
          u = msg.getMentionedUsers().get(0);
        }

        if (u.isBot()) {
          msg.getChannel().sendMessage("beep boop! nuu robot overlords pls thx~ <3").queue();
          return;
        }

        new Database().create_account(u.getIdLong());
        Document doc = new Database().get_account(u.getIdLong());
        
        if (doc == null) {
          msg.getChannel().sendMessage(new Logging().error(msg.getJDA().getSelfUser(), "Balance for the selected user was not found!")).queue();
          return;
        }
        
        MessageEmbed embed = new EmbedBuilder()
            .setAuthor(u.getAsTag(), "https://astolfo.tech", u.getAvatarUrl())
            .setFooter(System.getenv("VERSION_ID"), msg.getJDA().getSelfUser().getAvatarUrl())
            .setColor(0xde1073)
            .addField("Credits", doc.get("credits")+" <:credit:738537190652510299>", true)
            .addField("Trap Coins", doc.get("trapcoins")+" <:trapcoin:738537189884821606>", true)
            .addField("Tokens", doc.get("tokens")+" <:token:738537180003172354>", true)
            .build();
        msg.getChannel().sendMessage(embed).queue();
    }
}